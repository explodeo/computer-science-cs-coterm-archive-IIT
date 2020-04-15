using System.ComponentModel;

namespace FlightBooking.Models
{
    public class Address
    {
        // TODO: Add constraints to allowed values
        [DisplayName("Street Number")]
        public int StreetNumber { get; set; }
        [DisplayName("Street Name")]
        public string StreetName { get; set; }
        public string City { get; set; }
        public string State { get; set; }
        [DisplayName("Zip Code")]
        public string ZipCode { get; set; }
        public string Country { get; set; }
        public int AddressID { get; set; }
        // TODO: Add IEnumberable<Customer> for the customers the address is home to?

        public Address() { }

        public Address(int streetNumber, string streetName, string city, string state, string zipCode, string country, int addressID)
        {
            StreetNumber = streetNumber;
            StreetName = streetName;
            City = city;
            State = state;
            ZipCode = zipCode;
            Country = country;
            AddressID = addressID;
        }
        
        public Address(int streetNumber, string streetName, string city, string country, int addressID)
        {
            StreetNumber = streetNumber;
            StreetName = streetName;
            City = city;
            Country = country;
            AddressID = addressID;
        }
    }
}