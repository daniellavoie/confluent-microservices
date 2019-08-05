using System.Collections.Generic;

namespace Wallet.Model
{
    public class Wallet
    {
        public string Account { get; set; }
        public List<WalletEntry> Entries { get; set; }
    }
}