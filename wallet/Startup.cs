using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

using Wallet.Services;
using Wallet.Interfaces;
using Wallet.Repositories;
using Wallet.Infrastructure;

namespace Wallet
{
  public class Startup
  {
    readonly string AllowSpecificOrigins = "AllowSpecificOrigins";

    public Startup()
    {

    }

    // This method gets called by the runtime. Use this method to add services to the container.
    public void ConfigureServices(IServiceCollection services)
    {
      services.AddSingleton(typeof(IOperationRepository), typeof(OperationRepository));
      services.AddSingleton(typeof(IOperationService), typeof(OperationService));
      services.AddSingleton(typeof(IWalletRepository), typeof(WalletRepository));
      services.AddSingleton(typeof(IWalletService), typeof(WalletService));

      var corsOrigins = Environment.GetEnvironmentVariable("WALLET_CORS_ORIGINS");
      if (corsOrigins == null || corsOrigins.Equals(""))
      {
        corsOrigins = "http://localhost:4200";
      }
      services.AddCors(options =>
      {
        options.AddPolicy(AllowSpecificOrigins,
              builder =>
              {
                Console.Out.WriteLine("Configuring cors origin " + corsOrigins);

                builder.WithOrigins(corsOrigins).AllowAnyMethod();
              });
      });

      services.AddMvc().SetCompatibilityVersion(CompatibilityVersion.Version_2_2);

    }

    // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
    public void Configure(IApplicationBuilder app, IHostingEnvironment env)
    {
      if (env.IsDevelopment())
      {
        app.UseDeveloperExceptionPage();
      }
      else
      {
        // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
        app.UseHsts();
      }

      app.UseCors(AllowSpecificOrigins);
      app.UseMvc();

      app.ApplicationServices.GetService<IOperationService>().SubscribeToOperations();
    }
  }
}
