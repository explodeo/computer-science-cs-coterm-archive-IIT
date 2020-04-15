namespace FlightBooking.Models
{
    public class Airport
    {
        public string IataID { get; set; }
        public string AirportName { get; set; }
        public string Country { get; set; }
        public string State { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }

        public Airport(string iataID, string airportName, string country, string state, double latitude, double longitude)
        {
            IataID = iataID;
            AirportName = airportName;
            Country = country;
            State = state;
            Latitude = latitude;
            Longitude = longitude;
        }
    }
}