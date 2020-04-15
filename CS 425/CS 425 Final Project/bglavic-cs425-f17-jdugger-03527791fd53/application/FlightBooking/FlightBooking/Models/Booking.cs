using System.Collections.Generic;
using System.ComponentModel;

namespace FlightBooking.Models
{
    public class Booking
    {
        [DisplayName("Booking ID")]
        public int BookingID { get; set; }
        public string Email { get; set; }
        public string CcNumber { get; set; }
        [DisplayName("Flight Class")]
        public string FlightClass { get; set; }
        public IEnumerable<Flight> BookingFlights { get; set; }

        public Booking(int bookingID, string email, string ccNumber, string flightClass) : this(bookingID, email, ccNumber, flightClass, new List<Flight>())
        {
        }

        public Booking(int bookingID, string email, string ccNumber, string flightClass, IEnumerable<Flight> flights)
        {
            BookingID = bookingID;
            Email = email;
            CcNumber = ccNumber;
            FlightClass = flightClass;
            BookingFlights = flights;
        }
    }
}