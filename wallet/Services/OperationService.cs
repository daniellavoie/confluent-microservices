using System;
using System.IO;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Runtime.Serialization.Json;

using Confluent.Kafka;
using Newtonsoft.Json;

using Wallet.Model;
using Wallet.Interfaces;
using Wallet.Repositories;

namespace Wallet.Services
{
  using Wallet.Model;

  public class OperationService : IOperationService
  {
    private readonly IOperationRepository _operationRepository;
    private readonly IWalletService _walletService;

    private readonly CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    private readonly DataContractJsonSerializer serializer = new DataContractJsonSerializer(new Operation().GetType());

    private Boolean running = true;

    public OperationService(IOperationRepository operationRepository, IWalletService walletService)
    {
      _operationRepository = operationRepository;
      _walletService = walletService;

      Console.CancelKeyPress += (_, e) =>
      {
        Close();
      };
    }

    private void Close()
    {
      running = false;
      cancellationTokenSource.Cancel();
    }

    public void ProcessOperation(Operation operation)
    {
      Console.WriteLine("Processing operation for account " + operation.Account + ".");
      
      var wallet = _walletService.FindByAccount(operation.Account);
      if (wallet == null)
      {
        Console.WriteLine("Account not found. Creating a new one for " + operation.Account + ".");

        wallet = new Wallet();
        wallet.Account = operation.Account;
        wallet.Entries = new List<WalletEntry>();
      }

      var currenciesAmount = new Dictionary<string, float>();

      _operationRepository.Save(operation);
      List<Operation> operations = _operationRepository.FindByAccount(operation.Account);

      operations.ForEach(existingOperation =>
      {
        if (existingOperation.CreditCurrency != null && !existingOperation.CreditCurrency.Equals(""))
        {

          if (currenciesAmount.ContainsKey(existingOperation.CreditCurrency))
          {
            currenciesAmount[existingOperation.CreditCurrency] = currenciesAmount[existingOperation.CreditCurrency] + existingOperation.CreditAmount;
          }
          else
          {
            currenciesAmount.Add(existingOperation.CreditCurrency, existingOperation.CreditAmount);
          }

        }
        if (existingOperation.DebitCurrency != null && !existingOperation.DebitCurrency.Equals(""))
        {
          if (currenciesAmount.ContainsKey(existingOperation.DebitCurrency))
          {
            currenciesAmount[existingOperation.DebitCurrency] = currenciesAmount[existingOperation.DebitCurrency] - existingOperation.DebitAmount;
          }
          else
          {
            currenciesAmount.Add(existingOperation.DebitCurrency, existingOperation.DebitAmount);
          }
        }
      });

      List<WalletEntry> newEntries = new List<WalletEntry>();
      foreach (KeyValuePair<string, float> entry in currenciesAmount)
      {
        WalletEntry newEntry = new WalletEntry();
        newEntry.Balance = entry.Value;
        newEntry.Currency = entry.Key;
        newEntries.Add(newEntry);
      }

      wallet.Entries = newEntries;
      _walletService.Save(wallet);
    }

    private ConsumerConfig buildConsumerConfig()
    {

      var BootstrapServers = Environment.GetEnvironmentVariable("WALLET_KAFKA_BOOTSTRAP_SERVERS");
      var GroupId = Environment.GetEnvironmentVariable("WALLET_KAFKA_GROUP_ID");

      if (BootstrapServers == null || BootstrapServers.Equals(""))
      {
        BootstrapServers = "localhost:9092";
      }
      if (GroupId == null || GroupId.Equals(""))
      {
        GroupId = "wallet";
      }

      return new ConsumerConfig
      {
        GroupId = GroupId,
        BootstrapServers = BootstrapServers,
        AutoOffsetReset = AutoOffsetReset.Earliest
      };
    }

    public void SubscribeToOperations()
    {
      var ser = new DataContractJsonSerializer(typeof(Operation));
      Task.Run(RunConsumer);
    }

    public void ReadMessages(IConsumer<Ignore, string> consumer)
    {
      while (running)
      {
        try
        {
          ProcessOperation(JsonConvert.DeserializeObject<Operation>(consumer.Consume(cancellationTokenSource.Token).Value));
        }
        catch (ConsumeException e)
        {
          Console.WriteLine($"Error occured: {e.Error.Reason}");
        }
      }
    }

    public void RunConsumer()
    {
      if (running)
      {
        using (var consumer = new ConsumerBuilder<Ignore, string>(buildConsumerConfig()).Build())
        {
          consumer.Subscribe("operation");

          try
          {
            ReadMessages(consumer);
          }
          catch (Exception e)
          {
            Console.WriteLine(e.ToString());

            if (running)
            {
              Console.WriteLine($"Reconnecting to kafka in 5 seconds.");

              Thread.Sleep(5000);
            }
          }
          finally
          {
            consumer.Close();
          }
        }
      }
    }
  }
}