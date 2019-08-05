using System.Collections.Generic;

namespace Wallet.Repositories
{
    using Wallet.Model;
    public interface IOperationRepository
    {
        List<Operation> FindByAccount(string account);

        Operation Save(Operation operation);
    }
}