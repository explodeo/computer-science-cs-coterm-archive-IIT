using System.Collections.Generic;
using System.ComponentModel;

namespace FlightBooking.Models
{
    public class Customer
    {
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string Email { get; set; }
        [DisplayName("Airport")]
        public string IataID { get; set; }
        public IEnumerable<Address> LivesAt { get; set; }
        public IEnumerable<CreditCard> OwnsCreditCards { get; set; }

        public Customer() { }

        public Customer(string firstName, string lastName, string email, string iataID)
        {
            FirstName = firstName;
            LastName = lastName;
            Email = email;
            IataID = iataID;
            LivesAt = new List<Address>();
            OwnsCreditCards = new List<CreditCard>();
        }
    }
}