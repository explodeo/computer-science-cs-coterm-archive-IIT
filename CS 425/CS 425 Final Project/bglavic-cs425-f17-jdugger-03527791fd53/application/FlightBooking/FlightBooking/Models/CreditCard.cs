using System;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;

namespace FlightBooking.Models
{
    public class CreditCard
    {
        // TODO: input restrictions
        [DisplayName("Provider")]
        public string Type { get; set; }
        [DisplayName("Number")]
        public string CcNumber { get; set; }
        [DisplayName("First Name")]
        public string CardFirstName { get; set; }
        [DisplayName("Last Name")]
        public string CardLastName { get; set; }
        [DisplayName("Expiration")]
        [DisplayFormat(DataFormatString="{0:MM/yy}", ApplyFormatInEditMode = true)]
        public DateTime ExpirationDate { get; set; }
        [DisplayName("CVC")]
        public string Cvc { get; set; }
        public int AddressID { get; set; }
        public Address Address { get; set; }

        public CreditCard() { }

        public CreditCard(string type, string ccNumber, string cardFirstName, string cardLastName, DateTime expirationDate,
            string cvc, Address address)
        {
            Type = type;
            CcNumber = ccNumber;
            CardFirstName = cardFirstName;
            CardLastName = cardLastName;
            ExpirationDate = expirationDate;
            Cvc = cvc;
            Address = address;
        }

        public CreditCard(string type, string ccNumber, string cardFirstName, string cardLastName, DateTime expirationDate,
            string cvc, int addressID)
        {
            Type = type;
            CcNumber = ccNumber;
            CardFirstName = cardFirstName;
            CardLastName = cardLastName;
            ExpirationDate = expirationDate;
            Cvc = cvc;
            AddressID = addressID;
        }
    }
}