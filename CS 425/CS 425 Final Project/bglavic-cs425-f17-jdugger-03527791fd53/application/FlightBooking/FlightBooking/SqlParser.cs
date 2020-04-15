using System;
using System.Collections.Generic;
using System.Linq;
using FlightBooking.Models;
using Npgsql;

namespace FlightBooking
{
    public class SqlParser
    {
        public IEnumerable<Customer> ParseCustomer(NpgsqlDataReader reader)
        {
            var customers = new List<Customer>();
            var firstNameColumn = reader.GetOrdinal("firstname");
            var lastNameColumn = reader.GetOrdinal("lastname");
            var emailColumn = reader.GetOrdinal("email");
            var iataIDColumn = reader.GetOrdinal("iata_id");

            while (reader.Read())
            {
                var firstName = reader[firstNameColumn] as string;
                var lastName = reader[lastNameColumn] as string;
                var email = reader[emailColumn] as string;
                var iataID = reader[iataIDColumn] as string;

                customers.Add(new Customer(firstName, lastName, email, iataID));
            }

            return customers;
        }

        public IEnumerable<Flight> ParseFlight(NpgsqlDataReader reader)
        {
            var flights = new List<Flight>();
            var dateColumn = reader.GetOrdinal("date");
            var flightNumberColumn = reader.GetOrdinal("flightnumber");
            var departureTimeColumn = reader.GetOrdinal("departuretime");
            var arrivalTimeColumn = reader.GetOrdinal("arrivaltime");
            var departureAirportColumn = reader.GetOrdinal("departureairport");
            var arrivalAirportColumn = reader.GetOrdinal("arrivalairport");
            var airlineIDColumn = reader.GetOrdinal("airlineid");
            var maxCoachColumn = reader.GetOrdinal("maxcoach");
            var maxFirstColumn = reader.GetOrdinal("maxfirstclass");
            var bookedCoachColumn = reader.GetOrdinal("bookedcoach");
            var bookedFirstColumn = reader.GetOrdinal("bookedfirst");

            while(reader.Read())
            {
                var date = (reader[dateColumn] as DateTime?).GetValueOrDefault();
                var flightNumber = (reader[flightNumberColumn] as int?).GetValueOrDefault();
                var departureTime = (reader[departureTimeColumn] as TimeSpan?).GetValueOrDefault();
                var arrivalTime = (reader[arrivalTimeColumn] as TimeSpan?).GetValueOrDefault();
                var departureAirport = reader[departureAirportColumn] as string;
                var arrivalAirport = reader[arrivalAirportColumn] as string;
                var airlineID = reader[airlineIDColumn] as string;
                var maxCoach = (reader[maxCoachColumn] as int?).GetValueOrDefault();
                var maxFirst = (reader[maxFirstColumn] as int?).GetValueOrDefault();
                var bookedCoach = (reader[bookedCoachColumn] as int?).GetValueOrDefault();
                var bookedFirst = (reader[bookedFirstColumn] as int?).GetValueOrDefault();

                flights.Add(new Flight(date, flightNumber, departureTime, arrivalTime, departureAirport,
                    arrivalAirport, maxCoach, maxFirst, airlineID, bookedCoach, bookedFirst));
            }

            return flights;
        }

        public IEnumerable<string[]> ParseRoute(NpgsqlDataReader reader)
        {
            var routes = new List<string[]>();

            while (reader.Read())
            {
                routes.Add(reader["route"] as string[]);
            }

            return routes;
        }
        

        public IEnumerable<Address> ParseAddress(NpgsqlDataReader reader)
        {
            var addresses = new List<Address>();
            var streetNumberColumn = reader.GetOrdinal("streetnumber");
            var streetNameColumn = reader.GetOrdinal("streetname");
            var cityColumn = reader.GetOrdinal("city");
            var stateColumn = reader.GetOrdinal("state");
            var zipCodeColumn = reader.GetOrdinal("zipcode");
            var countryColumn = reader.GetOrdinal("country");
            var addressIDColumn = reader.GetOrdinal("addressid");

            while (reader.Read())
            {
                var streetNumber = (reader[streetNumberColumn] as int?).GetValueOrDefault();
                var streetName = reader[streetNameColumn] as string;
                var city = reader[cityColumn] as string;
                var state = reader[stateColumn] as string;
                var zipCode = reader[zipCodeColumn] as string;
                var country = reader[countryColumn] as string;
                var addressID = (reader[addressIDColumn] as int?).GetValueOrDefault();

                addresses.Add(new Address(streetNumber, streetName, city, state, zipCode, country, addressID));
            }

            return addresses;
        }

        public IEnumerable<Airline> ParseAirline(NpgsqlDataReader reader)
        {
            var airlines = new List<Airline>();
            var airlineIDColumn = reader.GetOrdinal("airlineid");
            var countryColumn = reader.GetOrdinal("country");
            var airlineNameColumn = reader.GetOrdinal("airlinename");

            while (reader.Read())
            {
                var airlineID = reader[airlineIDColumn] as string;
                var country = reader[countryColumn] as string;
                var airlineName = reader[airlineNameColumn] as string;

                airlines.Add(new Airline(airlineID, country, airlineName));
            }

            return airlines;
        }

        public IEnumerable<Airport> ParseAirport(NpgsqlDataReader reader)
        {
            var airports = new List<Airport>();
            var iataIDColumn = reader.GetOrdinal("iataid");
            var airportNameColumn = reader.GetOrdinal("airportname");
            var countryColumn = reader.GetOrdinal("country");
            var stateColumn = reader.GetOrdinal("state");
            var latitudeColumn = reader.GetOrdinal("latitude");
            var longitudeColumn = reader.GetOrdinal("longitude");

            while (reader.Read())
            {
                var iataID = reader[iataIDColumn] as string;
                var airportName = reader[airportNameColumn] as string;
                var country = reader[countryColumn] as string;
                var state = reader[stateColumn] as string;
                var latitude = (reader[latitudeColumn] as double?).GetValueOrDefault();
                var longitude = (reader[longitudeColumn] as double?).GetValueOrDefault();

                airports.Add(new Airport(iataID, airportName, country, state, latitude, longitude));
            }

            return airports;
        }

        public IEnumerable<CreditCard> ParseCreditCard(NpgsqlDataReader reader)
        {
            var creditCards = new List<CreditCard>();
            var typeColumn = reader.GetOrdinal("type");
            var ccNumberColumn = reader.GetOrdinal("ccnumber");
            var cardFirstNameColumn = reader.GetOrdinal("cardfirstname");
            var cardLastNameColumn = reader.GetOrdinal("cardlastname");
            var expirationDateColumn = reader.GetOrdinal("expirationdate");
            var cvcColumn = reader.GetOrdinal("cvc");
            var addressIDColumn = reader.GetOrdinal("addressid");

            while (reader.Read())
            {
                var type = reader[typeColumn] as string;
                var ccNumber = reader[ccNumberColumn] as string;
                var cardFirstName = reader[cardFirstNameColumn] as string;
                var cardLastName = reader[cardLastNameColumn] as string;
                var expirationDate = (reader[expirationDateColumn] as DateTime?).GetValueOrDefault();
                var cvc = reader[cvcColumn] as string;
                var addressID = reader.GetInt32(addressIDColumn);

                creditCards.Add(new CreditCard(type, ccNumber, cardFirstName, cardLastName, expirationDate, cvc, addressID));
            }

            return creditCards;
        }

        public IEnumerable<MileageProgram> ParseMileageProgram(NpgsqlDataReader reader)
        {
            var mileagePrograms = new List<MileageProgram>();
            var milesColumn = reader.GetOrdinal("miles");
            var emailColumn = reader.GetOrdinal("email");
            var airlineColumn = reader.GetOrdinal("airline");
            var bookingIDColumn = reader.GetOrdinal("bookingid");

            while (reader.Read())
            {
                var miles = (reader[milesColumn] as int?).GetValueOrDefault();
                var email = reader[emailColumn] as string;
                var airline = reader[airlineColumn] as string;
                var bookingID = reader.GetInt32(bookingIDColumn);

                mileagePrograms.Add(new MileageProgram(miles, email, airline, bookingID));
            }
            return mileagePrograms;
        }

        public IEnumerable<Price> ParsePrice(NpgsqlDataReader reader)
        {
            var prices = new List<Price>();
            var flightClassColumn = reader.GetOrdinal("flightclass");
            var costColumn = reader.GetOrdinal("cost");

            while (reader.Read())
            {
                var flightClass = reader[flightClassColumn] as string;
                var cost = (reader[costColumn] as decimal?).GetValueOrDefault();

                prices.Add(new Price(flightClass, cost));
            }

            return prices;
        }

        public IEnumerable<Booking> ParseBooking(NpgsqlDataReader reader)
        {
            var bookings = new List<Booking>();
            var bookingIDColumn = reader.GetOrdinal("bookingid");
            var emailColumn = reader.GetOrdinal("email");
            var ccNumberColumn = reader.GetOrdinal("ccnumber");
            var flightClassColumn = reader.GetOrdinal("flightclass");

            while (reader.Read())
            {
                var bookingID = (reader[bookingIDColumn] as int?).GetValueOrDefault();
                var email = reader[emailColumn] as string;
                var ccNumber = reader[ccNumberColumn] as string;
                var flightClass = reader[flightClassColumn] as string;

                bookings.Add(new Booking(bookingID, email, ccNumber, flightClass));
            }

            return bookings;
        }

        /*
        public IEnumerable< TODO > ParseCreditCardOwner(NpgsqlDataReader reader)
        {
            var owners = new List< TODO >();
            var emailColumn = reader.GetOrdinal("email");
            var ccNumberColumn = reader.GetOrdinal("ccnumber");

            while (reader.Read())
            {
                var email = reader[emailColumn] as string;
                var ccNumber = reader[ccNumberColumn] as string;

                owners.Add(new Customer(email, ccNumber));
            }

            return owners;
        }
        */
        // TODO: ParseCreditCardOwner
        // TODO: ParseLivesAt
        // TODO: ParseBookingFlights
    }
}