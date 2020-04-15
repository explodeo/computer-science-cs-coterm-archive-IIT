namespace FlightBooking.Models
{
    public class Airline
    {
        public string AirlineID { get; set; }
        public string Country { get; set; }
        public string AirlineName { get; set; }

        public Airline(string airlineID, string country, string airlineName)
        {
            AirlineID = airlineID;
            Country = country;
            AirlineName = airlineName;
        }
    }
}