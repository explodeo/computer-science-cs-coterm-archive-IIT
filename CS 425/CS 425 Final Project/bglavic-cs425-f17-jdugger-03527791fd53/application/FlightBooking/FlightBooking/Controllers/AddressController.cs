using System;
using System.Linq;
using System.Net;
using System.Web.Mvc;
using FlightBooking.Models;

namespace FlightBooking.Controllers
{
    public class AddressController : Controller
    {
        private static readonly SqlParser Parser = new SqlParser();
        private static readonly SqlClient Client = new SqlClient(Parser);

        [ChildActionOnly]
        public ActionResult Index()
        {           
            var addresses = Client.GetAddress(CurrentUser.Email);
            return PartialView("Index", addresses);
        }

        public ActionResult Create()
        {
            return View();
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "StreetNumber,StreetName,City,State,ZipCode,Country")] Address address)
        {
            try
            {
                if (ModelState.IsValid)
                {
                    Client.InsertCustomerAddress(CurrentUser.Email, address.StreetNumber, address.StreetName, address.City, address.State,
                        address.ZipCode, address.Country);
                    return RedirectToAction("Index", "Account");
                }
            }
            catch (Exception)
            {
                //Log the error (uncomment dex variable name and add a line here to write a log.)
                ModelState.AddModelError("", "Unable to save changes. Try again, and if the problem persists, see your system administrator.");
            }
            
            return View(address);
        }

        public ActionResult Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            var address = Client.GetAddress(id.Value)?.First();

            if (address == null)
            {
                return HttpNotFound();
            }
            return View(address);
        }

        [HttpPost, ActionName("Edit")]
        [ValidateAntiForgeryToken]
        public ActionResult EditPost(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            
            var address = Client.GetAddress(id.Value).First();

            if (TryUpdateModel(address, "", new[]{"StreetNumber", "StreetName", "City", "ZipCode", "Country"}))
            {
                try
                {
                    Client.UpdateAddress(address.StreetNumber, address.StreetName, address.City, address.State,
                        address.ZipCode, address.Country, address.AddressID);

                    return RedirectToAction("Index", "Account");
                }
                catch (Exception /* dex */)
                {
                    //Log the error (uncomment dex variable name and add a line here to write a log.
                    ModelState.AddModelError("", "Unable to save changes. Try again, and if the problem persists, see your system administrator.");
                }
            }

            return View(address);
        }

        public ActionResult Delete(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            
            var address = Client.GetAddress(id.Value)?.First();
            if (address == null)
            {
                return HttpNotFound();
            }
            return View(address);
        }

        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(int id)
        {
            Client.DeleteAddress(id);
            return RedirectToAction("Index", "Account");
        }
    }
}