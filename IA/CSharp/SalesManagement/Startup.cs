using System;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using SalesManagement.Data;

namespace SalesManagement
{
    public class Startup
    {
        public IConfiguration Configuration { get; }

        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public void ConfigureServices(
            IServiceCollection services)
        {
            string connectionString =
                Configuration.GetConnectionString(
                    "DefaultConnection"
                );

            if (string.IsNullOrWhiteSpace(connectionString))
            {
                throw new InvalidOperationException(
                    "Configure a variavel de ambiente " +
                    "ConnectionStrings__DefaultConnection."
                );
            }

            services.AddDbContext<AppDbContext>(options =>
                options.UseMySql(connectionString)
            );

            services.AddMvc()
                .SetCompatibilityVersion(
                    CompatibilityVersion.Version_2_1
                );
        }

        public void Configure(
            IApplicationBuilder app,
            IHostingEnvironment environment)
        {
            if (environment.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }
            else
            {
                app.UseExceptionHandler("/Home/Error");
            }

            app.UseStaticFiles();

            app.UseMvc(routes =>
            {
                routes.MapRoute(
                    name: "default",
                    template: "{controller=Home}/{action=Index}/{id?}"
                );
            });

            using (IServiceScope scope =
                app.ApplicationServices.CreateScope())
            {
                AppDbContext context =
                    scope.ServiceProvider
                        .GetRequiredService<AppDbContext>();

                context.Database.Migrate();

                DbInitializer.Initialize(context);
            }
        }
    }
}