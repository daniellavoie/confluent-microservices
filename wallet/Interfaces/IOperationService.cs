using Wallet.Model;

namespace Wallet.Interfaces
{
    public interface IOperationService
    {
        void ProcessOperation(Operation operation);

        void SubscribeToOperations();
    }
}