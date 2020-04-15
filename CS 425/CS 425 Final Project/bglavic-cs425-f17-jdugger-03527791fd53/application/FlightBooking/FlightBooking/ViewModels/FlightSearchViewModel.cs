using System;
using System.ComponentModel;

namespace FlightBooking.ViewModels
{
    public class FlightSearchViewModel
    {
        [DisplayName("From")]
        public string DepartureAirport { get; set; }
        [DisplayName("To")]
        public string ArrivalAirport { get; set; }
        [DisplayName("Departure Date")]
        public DateTime DepartureDate { get; set; }
        [DisplayName("Return Date")]
        public DateTime? ReturnDate { get; set; }
        [DisplayName("Round Trip")]
        public bool IsRoundTrip { get; set; }
        [DisplayName("Max Connections")]
        public int MaximumConnections { get; set; }
        [DisplayName("Max Time")]
        public TimeSpan MaximumTime { get; set; }
        [DisplayName("Max Price")]
        public decimal MaximumPrice { get; set; }
    }
}