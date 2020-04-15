using System;
using System.Linq;
using System.Web.Mvc;
using FlightBooking.ViewModels;

namespace FlightBooking.Controllers
{
    public class FlightSearchController : Controller
    {
        private static readonly SqlParser Parser = new SqlParser();
        private static readonly SqlClient Client = new SqlClient(Parser);

        // GET: FlightSearch
        public ActionResult Search()
        {
            return View();
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Search([Bind(Include = "DepartureAirport,ArrivalAirport,DepartureDate,ReturnDate,MaximumConnections,MaximumTime,MaximumPrice")] FlightSearchViewModel vm)
        {
            try
            {
                if (ModelState.IsValid)
                {
                    var connections = Client.GetRoutes(vm.DepartureDate, vm.DepartureAirport, vm.ArrivalAirport,
                        vm.MaximumConnections, vm.MaximumTime, vm.MaximumPrice);

                    var bookings = connections.Select(c => new FlightConnectionViewModel(c));
                    return View("Results", bookings);
                }
            }
            catch (Exception /* dex */)
            {
                //Log the error (uncomment dex variable name and add a line here to write a log.)
                ModelState.AddModelError("", "Unable to save changes. Try again, and if the problem persists, see your system administrator.");
            }

            return View(vm);
        }
    }
}