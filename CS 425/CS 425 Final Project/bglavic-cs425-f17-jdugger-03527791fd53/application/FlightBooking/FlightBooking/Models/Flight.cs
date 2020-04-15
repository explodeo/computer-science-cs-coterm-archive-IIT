using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;

namespace FlightBooking.Models
{
    public class Flight
    {
        public DateTime Date { get; set; }
        [DisplayName("Number")]
        public int FlightNumber { get; set; }
        [DisplayName("Departure")]
        [DisplayFormat(DataFormatString = "{0:hh\\:mm}", ApplyFormatInEditMode = true)]
        public TimeSpan DepartureTime { get; set; }
        [DisplayName("Arrival")]
        [DisplayFormat(DataFormatString = "{0:hh\\:mm}", ApplyFormatInEditMode = true)]
        public TimeSpan ArrivalTime { get; set; }
        public TimeSpan Length => ArrivalTime - DepartureTime;
        [DisplayName("From")]
        public string DepartureAirport { get; set; }
        [DisplayName("To")]
        public string ArrivalAirport { get; set; }
        public int MaxCoach { get; set; }
        public int MaxFirstClass { get; set; }
        [DisplayName("Airline")]
        public string AirlineID { get; set; }
        public int BookedCoach { get; set; }
        public int BookedFirstClass { get; set; }
        public IEnumerable<Price> Prices { get; set; }

        public Flight(DateTime date, int flightNumber, TimeSpan departureTime,
        TimeSpan arrivalTime, string departureAirport, string arrivalAirport,
        int maxCoach, int maxFirstClass, string airlineID, int bookedCoach, int bookedFirstClass)
        {
            Date = date;
            FlightNumber = flightNumber;
            DepartureTime = departureTime;
            ArrivalTime = arrivalTime;
            DepartureAirport = departureAirport;
            ArrivalAirport = arrivalAirport;
            MaxCoach = maxCoach;
            MaxFirstClass = maxFirstClass;
            AirlineID = airlineID;
            BookedCoach = bookedCoach;
            BookedFirstClass = bookedFirstClass;
        }
    }
}