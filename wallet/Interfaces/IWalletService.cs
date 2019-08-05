namespace Wallet.Interfaces
{
    using Wallet.Model;

    public interface IWalletService
    {
        Wallet FindByAccount(string account);

        Wallet Save(Wallet wallet);
    }
}