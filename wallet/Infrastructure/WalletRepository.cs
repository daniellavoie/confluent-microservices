using System.Collections.Generic;
using Wallet.Repositories;


namespace Wallet.Infrastructure
{
    using Wallet.Model;
    public class WalletRepository : IWalletRepository
    {
        private readonly Dictionary<string, Wallet> walletsByAccount = new Dictionary<string, Wallet>();

        public Wallet FindByAccount(string account)
        {
            return walletsByAccount[account];
        }

        public Wallet Save(Wallet wallet)
        {
            walletsByAccount[wallet.Account] = wallet;

            return wallet;
        }
    }
}