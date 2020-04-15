using System;
using System.Linq;
using System.Net;
using System.Web.Mvc;
using FlightBooking.Models;
using Microsoft.Ajax.Utilities;

namespace FlightBooking.Controllers
{
    public class CreditCardController : Controller
    {
        private static readonly SqlParser Parser = new SqlParser();
        private static readonly SqlClient Client = new SqlClient(Parser);

        [ChildActionOnly]
        public ActionResult Index()
        {
            var creditCards = Client.GetCreditCards(CurrentUser.Email);
            return PartialView("Index", creditCards);
        }

        public ActionResult Create()
        {
            return View();
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "Type,CcNumber,CardFirstName,CardLastName,ExpirationDate,Cvc,AddressID")] CreditCard creditCard)
        {
            try
            {
                if (ModelState.IsValid)
                {
                    Client.InsertCreditCard(CurrentUser.Email, creditCard.Type, creditCard.CcNumber, creditCard.CardFirstName,
                        creditCard.CardLastName, creditCard.ExpirationDate, creditCard.Cvc, creditCard.AddressID);

                    return RedirectToAction("Index", "Account");
                }
            }
            catch (Exception /* dex */)
            {
                //Log the error (uncomment dex variable name and add a line here to write a log.)
                ModelState.AddModelError("", "Unable to save changes. Try again, and if the problem persists, see your system administrator.");
            }

            return RedirectToAction("Index", "Account");
        }

        public ActionResult Edit(string id)
        {
            if (id.IsNullOrWhiteSpace())
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            var creditCard = Client.GetCreditCard(id);

            if (creditCard == null)
            {
                return HttpNotFound();
            }
            return View(creditCard);
        }

        [HttpPost, ActionName("Edit")]
        [ValidateAntiForgeryToken]
        public ActionResult EditPost(string id)
        {
            if (id.IsNullOrWhiteSpace())
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            
            var creditCard = Client.GetCreditCard(id);
            if (TryUpdateModel(creditCard, "", new[] { "Type", "CcNumber", "CardFirstName", "CardLastName", "ExpirationDate", "Cvc", "AddressID" }))
            {
                try
                {
                    Client.UpdateCreditCard(creditCard.Type, creditCard.CcNumber, creditCard.CardFirstName,
                        creditCard.CardLastName, creditCard.ExpirationDate, creditCard.Cvc, creditCard.AddressID);

                    return RedirectToAction("Index", "Account");
                }
                catch (Exception /* dex */)
                {
                    //Log the error (uncomment dex variable name and add a line here to write a log.
                    ModelState.AddModelError("", "Unable to save changes. Try again, and if the problem persists, see your system administrator.");
                }
            }

            return View(creditCard);
        }

        public ActionResult Delete(string id)
        {
            if (id.IsNullOrWhiteSpace())
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            
            var creditCard = Client.GetCreditCard(id);
            if (creditCard == null)
            {
                return HttpNotFound();
            }
            return View(creditCard);
        }

        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(string id)
        {
            Client.DeleteCreditCard(CurrentUser.Email, id);
            return RedirectToAction("Index", "Account");
        }
    }
}