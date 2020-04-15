using System;

namespace FlightBooking.Models
{
    public class Price
    {
        public string FlightClass { get; set; }
        public decimal Cost { get; set; }

        public Price(string flightClass, decimal cost)
        {
            FlightClass = flightClass;
            Cost = cost;
        }
    }
}