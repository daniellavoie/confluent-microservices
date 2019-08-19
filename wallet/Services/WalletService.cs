using System;
using System.Collections.Generic;

using Wallet.Interfaces;
using Wallet.Repositories;

namespace Wallet.Services
{
  using Wallet.Model;
  public class WalletService : IWalletService
  {
    private readonly IWalletRepository _walletRepository;

    public WalletService(IWalletRepository walletRepository)
    {
      _walletRepository = walletRepository;
    }

    public Wallet FindByAccount(string account)
    {
      try
      {
        Console.WriteLine("Fetching account " + account + ".");

        return _walletRepository.FindByAccount(account);
      }
      catch (KeyNotFoundException)
      {
        return null;
      }
    }

    public Wallet Save(Wallet wallet)
    {
      Console.WriteLine("Persisting wallet state for account " + wallet.Account + ".");

      return _walletRepository.Save(wallet);
    }
  }
}