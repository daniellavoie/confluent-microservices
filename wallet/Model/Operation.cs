namespace Wallet.Model
{
    public class Operation
    {
        public string Account { get; set; }
        public string Type { get; set; }
        public float DebitAmount { get; set; }
        public string DebitCurrency { get; set; }
        public float CreditAmount { get; set; }
        public string CreditCurrency { get; set; }
    }
}