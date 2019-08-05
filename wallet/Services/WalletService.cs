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
                return _walletRepository.FindByAccount(account);
            }
            catch (KeyNotFoundException)
            {
                return null;
            }
        }

        public Wallet Save(Wallet wallet)
        {
            return _walletRepository.Save(wallet);
        }
    }
}