using Microsoft.AspNetCore.Mvc;

using Wallet.Interfaces;

namespace Wallet.Controllers.Api
{
    [Route("api/[Controller]")]
    public class WalletController : Controller
    {
        private readonly IWalletService _walletService;

        public WalletController(IWalletService walletService)
        {
            _walletService = walletService;
        }

        [HttpGet("{account}")]
        public JsonResult GetByAccount(string account)
        {
            return Json(_walletService.FindByAccount(account));
        }
    }
}