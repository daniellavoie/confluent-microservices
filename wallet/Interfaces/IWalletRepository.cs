namespace Wallet.Repositories
{
    using Wallet.Model;

    public interface IWalletRepository
    {
        Wallet FindByAccount(string account);

        Wallet Save(Wallet wallet);
    }
}