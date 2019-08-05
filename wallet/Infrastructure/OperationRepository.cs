using System.Collections.Generic;
using Wallet.Model;
using Wallet.Repositories;

namespace Wallet.Infrastructure
{
    public class OperationRepository : IOperationRepository
    {
        private readonly Dictionary<string, List<Operation>> operationsByAccount = new Dictionary<string, List<Operation>>();

        public List<Operation> FindByAccount(string account)
        {
            return operationsByAccount[account];
        }

        public Operation Save(Operation operation)
        {
            List<Operation> operations;
            try{
                operations = operationsByAccount[operation.Account];
            } catch(KeyNotFoundException){
                operations = new List<Operation>();
                operationsByAccount.Add(operation.Account, operations);
            }

            operations.Add(operation);

            return operation;
        }
    }
}