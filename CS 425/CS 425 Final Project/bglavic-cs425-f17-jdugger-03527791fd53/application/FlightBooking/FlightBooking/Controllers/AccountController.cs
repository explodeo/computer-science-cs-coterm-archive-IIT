using System.Linq;
using System.Web.Mvc;
using FlightBooking.Models;
using FlightBooking.ViewModels;

namespace FlightBooking.Controllers
{
    public class AccountController : Controller
    {
        private static readonly SqlParser Parser = new SqlParser();
        private static readonly SqlClient Client = new SqlClient(Parser);

        // GET: Account
        public ActionResult Index()
        {
            var email = CurrentUser.Email;
            if (string.IsNullOrWhiteSpace(email))
                return RedirectToAction("Login", "Account");

            var customer = Client.GetCustomer(email);
            return View(customer);
        }

        [HttpPost, ActionName("Index")]
        public ActionResult Index([Bind(Include = "IataID")] Customer customer)
        {
            Client.UpdateCustomer(CurrentUser.Email, customer.IataID);
            return View();
        }

        public ActionResult Login()
        {
            return View();
        }

        [HttpPost, ActionName("Login")]
        [ValidateAntiForgeryToken]
        public ActionResult LoginConfirmed([Bind(Include = "Email,FirstName,LastName")] LoginViewModel vm)
        {
            if (!string.IsNullOrWhiteSpace(CurrentUser.Email))
            {
                return RedirectToAction("Index");
            }

            var customer = Client.GetCustomer(vm.Email);
            if (customer != null)
            {
                // TODO: insert new customer
                CurrentUser.Email = vm.Email;
            }
            else
            {
                // log in
                if (Client.GetCustomer(vm.Email, vm.FirstName, vm.LastName).Any())
                {
                    CurrentUser.Email = vm.Email;
                }
            }

            return RedirectToAction("Index");
        }
    }
}