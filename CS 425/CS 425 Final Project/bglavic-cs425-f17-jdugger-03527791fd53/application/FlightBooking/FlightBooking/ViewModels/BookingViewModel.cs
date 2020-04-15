using System.Collections.Generic;
using FlightBooking.Models;

namespace FlightBooking.ViewModels
{
    public class BookingViewModel
    {
        public IEnumerable<Flight> Flights;
        public CreditCard CreditCard { get; set; }
    }
}