using System;
using System.Collections.Generic;
using FlightBooking.Models;
using Npgsql;
using System.Linq;

namespace FlightBooking
{
    public class SqlClient
    {
        private readonly string _connString;
        private readonly SqlParser _sqlParser;

        public SqlClient(SqlParser sqlParser)
        {
            _connString = string.Format("Server={0};Port={1};User Id={2};Password={3};Database={4}",
                "cs425project.cfo2e3troldi.us-east-2.rds.amazonaws.com", "5432", "CS425project", "CS425project",
                "CS425project");
            _sqlParser = sqlParser;
        }

        #region SQL SELECTS

        public IEnumerable<Customer> GetCustomer(string email, string firstName, string lastName)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "SELECT * FROM customer WHERE email = @email AND firstname = @firstname AND lastname = @lastname";
                    cmd.Parameters.AddWithValue("email", email);
                    cmd.Parameters.AddWithValue("firstname", firstName);
                    cmd.Parameters.AddWithValue("lastname", lastName);

                    using (var reader = cmd.ExecuteReader())
                    {
                        return _sqlParser.ParseCustomer(reader);
                    }
                }
            }
        }

        public Customer GetCustomer(string email)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();
                
                using (var cmd = new NpgsqlCommand())
                {
                    Customer customer;

                    cmd.Connection = conn;
                    cmd.CommandText =
                        "SELECT * FROM customer WHERE email = @email";
                    cmd.Parameters.AddWithValue("email", email);

                    using (var reader = cmd.ExecuteReader())
                    {
                        customer = _sqlParser.ParseCustomer(reader).First();
                    }

                    cmd.CommandText =
                        "SELECT * FROM address WHERE addressid IN (SELECT addressid FROM livesat WHERE email = @email)";
                    cmd.Parameters.AddWithValue("email", email);

                    using (var reader = cmd.ExecuteReader())
                    {
                        customer.LivesAt = _sqlParser.ParseAddress(reader);
                    }
                    
                    cmd.CommandText =
                        "SELECT * FROM creditcard WHERE ccnumber IN (SELECT ccnumber FROM creditcardowner WHERE email = @email)";
                    cmd.Parameters.AddWithValue("email", email);

                    using (var reader = cmd.ExecuteReader())
                    {
                        customer.OwnsCreditCards = _sqlParser.ParseCreditCard(reader);
                    }

                    return customer;
                }
            }
        }

        public IEnumerable<Address> GetAddress(int addressID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT * FROM address WHERE addressid = @addressid";
                    cmd.Parameters.AddWithValue("addressid", addressID);

                    using (var reader = cmd.ExecuteReader())
                    {
                        return _sqlParser.ParseAddress(reader);
                    }
                }
            }
        }

        public IEnumerable<Address> GetAddress(int streetNumber, string streetName, string city, string zipCode, string country, int addressID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT * FROM address WHERE streetnumber = @streeetnumber AND streetname = @streetname" +
                        "AND city = @city AND zipcode = @zipcode AND country = @country AND addressid = @addressid";
                    cmd.Parameters.AddWithValue("streetnumber", streetNumber);
                    cmd.Parameters.AddWithValue("streetname", streetName);
                    cmd.Parameters.AddWithValue("city", city);
                    cmd.Parameters.AddWithValue("zipcode", zipCode);
                    cmd.Parameters.AddWithValue("country", country);
                    cmd.Parameters.AddWithValue("addressid", addressID);

                    using (var reader = cmd.ExecuteReader())
                    {
                        return _sqlParser.ParseAddress(reader);
                    }
                }
            }
        }

        public IEnumerable<Address> GetAddress(string email)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT streetnumber, streetname, city, state, zipcode, country, addressid " +
                                      "FROM livesat NATURAL JOIN address " +
                                      "WHERE email = @email;";
                    cmd.Parameters.AddWithValue("email", email);

                    using (var reader = cmd.ExecuteReader())
                    {
                        return _sqlParser.ParseAddress(reader);
                    }
                }
            }
        }

        public IEnumerable<Airline> GetAirline(string airlineID, string country, string airlineName)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT * FROM airline WHERE airlineid = @airlineid AND country = @country AND airlinename = @airlinename";
                    cmd.Parameters.AddWithValue("airlineid", airlineID);
                    cmd.Parameters.AddWithValue("country", country);
                    cmd.Parameters.AddWithValue("airlinename", airlineName);

                    using (var reader = cmd.ExecuteReader())
                    {
                        return _sqlParser.ParseAirline(reader);
                    }
                }
            }
        }

        public Airport GetAirport(string iataID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT * FROM airport WHERE iata_id = @iata_id";
                    cmd.Parameters.AddWithValue("iata_id", iataID);
                    using (var reader = cmd.ExecuteReader())
                    {
                        return _sqlParser.ParseAirport(reader).First();
                    }
                }
            }
        }

        public Booking GetBooking(int bookingID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                Booking booking;
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT * FROM booking WHERE bookingid = @bookingid;";
                    cmd.Parameters.AddWithValue("bookingid", bookingID);

                    using (var reader = cmd.ExecuteReader())
                    {
                        booking = _sqlParser.ParseBooking(reader).First();
                    }

                    cmd.CommandText = "SELECT * FROM flight WHERE (date, flightnumber, airlineid) IN (SELECT date, flightnumber, airlineid FROM bookingflights WHERE bookingid = @bookingid);";
                    cmd.Parameters.AddWithValue("bookingid", bookingID);
                    using (var reader = cmd.ExecuteReader())
                    {
                        booking.BookingFlights = _sqlParser.ParseFlight(reader);
                    }

                    return booking;
                }
            }
        }

        public IEnumerable<Booking> GetBooking(string email)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                IEnumerable<Booking> bookings;
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT * FROM booking WHERE email = @email;";
                    cmd.Parameters.AddWithValue("email", email);

                    using (var reader = cmd.ExecuteReader())
                    {
                        bookings = _sqlParser.ParseBooking(reader);
                    }

                    foreach (var booking in bookings)
                    {
                        cmd.CommandText = "SELECT * FROM flight WHERE (date, flightnumber, airlineid) IN (SELECT date, flightnumber, airlineid FROM bookingflights WHERE bookingid = @bookingid);";
                        cmd.Parameters.AddWithValue("bookingid", booking.BookingID);
                        using (var reader = cmd.ExecuteReader())
                        {
                            booking.BookingFlights = _sqlParser.ParseFlight(reader);
                        }

                        foreach (var flight in booking.BookingFlights)
                        {
                            cmd.CommandText = "SELECT * FROM price WHERE date = @date AND flightnumber = @flightnumber AND airlineid = @airlineid;";
                            cmd.Parameters.AddWithValue("date", flight.Date);
                            cmd.Parameters.AddWithValue("flightnumber", flight.FlightNumber);
                            cmd.Parameters.AddWithValue("airlineid", flight.AirlineID);

                            using (var reader = cmd.ExecuteReader())
                            {
                                flight.Prices = _sqlParser.ParsePrice(reader);
                            }
                        }
                    }

                    return bookings;
                }
            }
        }

        public IEnumerable<Booking> GetBooking(int bookingID, string email, string ccNumber, string flightClass)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                IEnumerable<Booking> bookings;
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT * FROM booking WHERE email = @email;";
                    cmd.Parameters.AddWithValue("email", email);

                    using (var reader = cmd.ExecuteReader())
                    {
                        bookings = _sqlParser.ParseBooking(reader);
                    }

                    foreach (Booking booking in bookings)
                    {
                        cmd.CommandText = "SELECT * FROM flight WHERE (date, flightnumber, airlineid) IN (SELECT date, flightnumber, airlineid FROM bookingflights WHERE bookingid = @bookingid);";
                        cmd.Parameters.AddWithValue("bookingid", booking.BookingID);
                        using (var reader = cmd.ExecuteReader())
                        {
                            booking.BookingFlights = _sqlParser.ParseFlight(reader);
                        }
                    }

                    return bookings;
                }
            }
        }

        public CreditCard GetCreditCard(string ccNumber)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                CreditCard creditCard;
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT * FROM creditcard WHERE ccnumber = @ccnumber;";
                    cmd.Parameters.AddWithValue("ccnumber", ccNumber);

                    using (var reader = cmd.ExecuteReader())
                    {
                        creditCard = _sqlParser.ParseCreditCard(reader).First();
                    }

                    cmd.CommandText = "SELECT * FROM address WHERE addressid = @addressid;";
                    cmd.Parameters.AddWithValue("addressid", creditCard.AddressID);

                    using (var reader = cmd.ExecuteReader())
                    {
                        creditCard.Address = _sqlParser.ParseAddress(reader).First();
                    }

                    return creditCard;
                }
            }
        }

        public IEnumerable<CreditCard> GetCreditCards(string email)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT ccnumber, type, cardfirstname, cardlastname, expirationdate, cvc, addressid " +
                                      "FROM creditcardowner NATURAL JOIN creditcard " +
                                      "WHERE email = @email";
                    cmd.Parameters.AddWithValue("email", email);

                    using (var reader = cmd.ExecuteReader())
                    {
                        return _sqlParser.ParseCreditCard(reader);
                    }
                }
            }
        }

        public IEnumerable<CreditCard> GetCreditCard(string type, string ccNumber, string cardFirstName, string cardLastName, 
            DateTime expirationDate, string cvc, int addressID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT * FROM creditcard WHERE type = @type AND ccnumber = @ccnumber AND cardfirstname = @cardfirstname" +
                        "AND cardlastname = @cardlastname AND expirationdate = @expirationdate AND cvc = @cvc AND addressid = @addressid";
                    cmd.Parameters.AddWithValue("type", type);
                    cmd.Parameters.AddWithValue("ccnumber", ccNumber);
                    cmd.Parameters.AddWithValue("cardfirstname", cardFirstName);
                    cmd.Parameters.AddWithValue("cardlastname", cardLastName);
					cmd.Parameters.AddWithValue("expirationdate", expirationDate);
                    cmd.Parameters.AddWithValue("cvc", cvc);
                    cmd.Parameters.AddWithValue("addressid", addressID);

                    using (var reader = cmd.ExecuteReader())
                    {
                        return _sqlParser.ParseCreditCard(reader);
                    }
                }
            }
        }

        public Flight GetFlight(DateTime date, int flightNumber, string airlineID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                Flight flight;
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT * FROM flight WHERE date = @date AND flightnumber = @flightnumber AND airlineid = @airlineid;";
                    cmd.Parameters.AddWithValue("date", date);
                    cmd.Parameters.AddWithValue("flightnumber", flightNumber);
                    cmd.Parameters.AddWithValue("airlineid", airlineID);

                    using (var reader = cmd.ExecuteReader())
                    {
                        flight = _sqlParser.ParseFlight(reader).First();
                    }

                    cmd.CommandText = "SELECT * FROM price WHERE date = @date AND flightnumber = @flightnumber AND airlineid = @airlineid;";
                    cmd.Parameters.AddWithValue("date", date);
                    cmd.Parameters.AddWithValue("flightnumber", flightNumber);
                    cmd.Parameters.AddWithValue("airlineid", airlineID);

                    using (var reader = cmd.ExecuteReader())
                    {
                        flight.Prices = _sqlParser.ParsePrice(reader);
                    }

                    return flight;
                }
            }
        }

        public IEnumerable<IEnumerable<Flight>> GetRoutes(DateTime date, string departureAirport, string arrivalAirport, int maxConnections, TimeSpan maxTime, decimal maxPrice)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();
                var allRoutes = new List<IEnumerable<Flight>>();
                IEnumerable<string[]> routesArray;

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "WITH RECURSIVE connections AS (" +
                        "SELECT *, 1 path_length, ARRAY[airlineid, flightnumber]::text[] AS route FROM flight WHERE departureairport = @departureairport AND(bookedcoach < maxcoach OR bookedfirst < maxfirstclass) AND date = @date " +
                        "UNION " +
                        "SELECT two.date, two.flightnumber, two.departureTime, two.maxcoach, two.maxfirstclass, two.arrivalTime, connections.departureairport, two.arrivalairport, two.airlineid, " +
                            "two.bookedcoach, two.bookedfirst, path_length + 1 AS path_length, route || ARRAY[two.airlineid, two.flightnumber]::text[] AS route " +
                        "FROM connections, flight two " +
                        "WHERE connections.arrivalairport = two.departureairport AND path_length < @maxconnections AND two.date = @date AND(two.bookedcoach < two.maxcoach OR two.bookedfirst < two.maxfirstclass) AND(connections.arrivaltime - '00:30:00') > two.departuretime" +
                        ") " +
                        "SELECT route FROM connections WHERE arrivalairport = @arrivalairport; ";
                    cmd.Parameters.AddWithValue("date", date);
                    cmd.Parameters.AddWithValue("departureairport", departureAirport);
                    cmd.Parameters.AddWithValue("arrivalairport", arrivalAirport);
                    cmd.Parameters.AddWithValue("maxconnections", maxConnections+1);

                    using (var reader = cmd.ExecuteReader())
                    {
                        routesArray = _sqlParser.ParseRoute(reader);
                    }
                    
                    foreach (string[] r in routesArray)
                    {
                        var route = new List<Flight>();
                        decimal firstCost = 0;
                        decimal coachCost = 0;
                        for (int i = 0; i < r.Length; i = i + 2)
                        {
                            string airlineID = r[i];
                            int flightNumber = Int32.Parse(r[i + 1]);
                            

                            var flight = GetFlight(date, flightNumber, airlineID);
                            route.Add(flight);
                            if (flight.Prices.First().FlightClass == "First")
                            {
                                firstCost += flight.Prices.First().Cost;
                                coachCost += flight.Prices.Last().Cost;
                            }
                            else
                            {
                                firstCost += flight.Prices.Last().Cost;
                                coachCost += flight.Prices.First().Cost;
                            }
                        }
                        if ((route.Last().ArrivalTime - route.First().DepartureTime) < maxTime
                            && (coachCost <= maxPrice || firstCost <= maxPrice))
                        {
                            allRoutes.Add(route);
                        }
                    }

                    return allRoutes;
                }
            }
        }

        public IEnumerable<MileageProgram> GetMileageProgram(int miles, string email, string airline, int bookingID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT * FROM mileageprogram WHERE miles = @miles AND email = @email AND airline = @airline AND " +
                        "bookingid = @bookingid";
                    cmd.Parameters.AddWithValue("miles", miles);
                    cmd.Parameters.AddWithValue("email", email);
                    cmd.Parameters.AddWithValue("airline", airline);
                    cmd.Parameters.AddWithValue("bookingid", bookingID);

                    using (var reader = cmd.ExecuteReader())
                    {
                        return _sqlParser.ParseMileageProgram(reader);
                    }
                }
            }
        }

        public IEnumerable<Price> GetPrice(string flightClass, decimal cost, DateTime date, int flightNumber, string airline)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = "SELECT * FROM price WHERE flightclass = @flightclass AND cost = @cost AND date = @date AND airline = @airline";
                    cmd.Parameters.AddWithValue("flightclass", flightClass);
                    cmd.Parameters.AddWithValue("cost", cost);
                    cmd.Parameters.AddWithValue("date", date);
                    cmd.Parameters.AddWithValue("", flightNumber);
					cmd.Parameters.AddWithValue("airline", airline);

                    using (var reader = cmd.ExecuteReader())
                    {
                        return _sqlParser.ParsePrice(reader);
                    }
                }
            }
        }
        #endregion

        #region SQL INSERTS

        public void InsertCustomer(string firstName, string lastName, string email, string iataID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "INSERT INTO customer (firstname, lastname, email, iata_id) VALUES (@firstname, @lastname, @email, @iata_id);";
                    cmd.Parameters.AddWithValue("firstname", firstName);
                    cmd.Parameters.AddWithValue("lastname", lastName);
                    cmd.Parameters.AddWithValue("email", email);
                    cmd.Parameters.AddWithValue("iata_id", iataID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void InsertAirport(string iataID, string airportName, string country, string state, double latitude, double longitude)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "INSERT INTO airport (iata_id, airportname, country, state, latitude, longitude) " +
                        "VALUES (@iata_id, @airportname, @country, @state, @latitude, @longitude);";
                    cmd.Parameters.AddWithValue("iata_id", iataID);
                    cmd.Parameters.AddWithValue("airportname", airportName);
                    cmd.Parameters.AddWithValue("country", country);
                    cmd.Parameters.AddWithValue("state", state);
                    cmd.Parameters.AddWithValue("latitude", latitude);
                    cmd.Parameters.AddWithValue("longitude", longitude);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public int InsertCustomerAddress(string email, int streetNumber, string streetName, string city, string state, string zipCode, string country)
        {
            var addressID = InsertAddress(streetNumber, streetName, city, state, zipCode, country);
            InsertLivesAt(email, addressID);
            return addressID;
        }

        public int InsertAddress(int streetNumber, string streetName, string city, string state, string zipCode, string country)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                int addressID;
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText = state != null ?
                        "INSERT INTO address (streetnumber, streetname, city, state, zipcode, country) " +
                        "VALUES (@streetnumber, @streetname, @city, @state, @zipcode, @country) RETURNING addressid;"
                        : "INSERT INTO address (streetnumber, streetname, city, country) " +
                          "VALUES (@streetnumber, @streetname, @city, @country) RETURNING addressid;";
                    cmd.Parameters.AddWithValue("streetnumber", streetNumber);
                    cmd.Parameters.AddWithValue("streetname", streetName);
                    cmd.Parameters.AddWithValue("city", city);
                    cmd.Parameters.AddWithValue("state", state ?? "");
                    cmd.Parameters.AddWithValue("zipcode", zipCode ?? "");
                    cmd.Parameters.AddWithValue("country", country);
                    addressID = (int) cmd.ExecuteScalar();
                }

                return addressID;
            }
        }

        public void InsertCreditCard(string email, string type, string ccNumber, string cardFirstName, string cardLastName, DateTime expirationDate,
            string cvc, int addressID)
        {
            InsertCreditCard(type, ccNumber, cardFirstName, cardLastName, expirationDate,
            cvc, addressID);
            InsertCreditCardOwner(email, ccNumber);
        }

        public void InsertCreditCard(string email, string type, string ccNumber, string cardFirstName, string cardLastName, DateTime expirationDate,
            string cvc, Address address)
        {
            InsertCreditCard(type, ccNumber, cardFirstName, cardLastName, expirationDate,
            cvc, address.AddressID);
            InsertCreditCardOwner(email, ccNumber);
        }

        public void InsertCreditCard(string type, string ccNumber, string cardFirstName, string cardLastName, DateTime expirationDate,
            string cvc, int addressID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "INSERT INTO creditcard (type, ccnumber, cardfirstname, cardlastname, expirationdate, cvc, addressid) " +
                        "VALUES (@type, @ccnumber, @cardfirstname, @cardlastname, @expirationdate, @cvc, @addressid);";
                    cmd.Parameters.AddWithValue("type", type);
                    cmd.Parameters.AddWithValue("ccnumber", ccNumber);
                    cmd.Parameters.AddWithValue("cardfirstname", cardFirstName);
                    cmd.Parameters.AddWithValue("cardlastname", cardLastName);
                    cmd.Parameters.AddWithValue("expirationdate", expirationDate.Date);
                    cmd.Parameters.AddWithValue("cvc", cvc);
                    cmd.Parameters.AddWithValue("addressid", addressID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void InsertAirline(string airlineID, string country, string airlineName)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "INSERT INTO airline (airlineid, country, airlinename) " +
                        "VALUES (@airlineid, @country, @airlinename);";
                    cmd.Parameters.AddWithValue("airlineid", airlineID);
                    cmd.Parameters.AddWithValue("country", country);
                    cmd.Parameters.AddWithValue("airlinename", airlineName);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public int InsertBooking(string email, string ccNumber, string flightClass, IEnumerable<Flight> flights)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;

                    // Make sure none of the flights have become full while looking at results
                    foreach(Flight flight in flights)
                    {
                        int? max = 0, booked = 0;
                        if (flightClass == "First")
                        {
                            cmd.CommandText =
                                "SELECT maxfirstclass, bookedfirst FROM flight WHERE date = @date AND flightNumber = @flightnumber AND airlineid = @airlineid;";
                            cmd.Parameters.AddWithValue("date", flight.Date);
                            cmd.Parameters.AddWithValue("flightnumber", flight.FlightNumber);
                            cmd.Parameters.AddWithValue("airlineid", flight.AirlineID);
                            using (var reader = cmd.ExecuteReader())
                            {
                                max = reader[reader.GetOrdinal("maxfirstclass")] as int?;
                                booked = reader[reader.GetOrdinal("bookedfirst")] as int?;
                            }
                        } else if(flightClass == "Coach")
                        {
                            cmd.CommandText =
                                "SELECT maxcoach, bookedcoach FROM flight WHERE date = @date AND flightNumber = @flightnumber AND airlineid = @airlineid;";
                            cmd.Parameters.AddWithValue("date", flight.Date);
                            cmd.Parameters.AddWithValue("flightnumber", flight.FlightNumber);
                            cmd.Parameters.AddWithValue("airlineid", flight.AirlineID);
                            using (var reader = cmd.ExecuteReader())
                            {
                                max = reader[reader.GetOrdinal("maxcoach")] as int?;
                                booked = reader[reader.GetOrdinal("bookedcoach")] as int?;
                            }
                        }
                        if (booked == null || max == null || booked >= max)
                        {
                            return -1;
                        }
                    }

                    // Insert into the booking table
                    cmd.CommandText =
                        "INSERT INTO booking (email, ccnumber, flightclass) " +
                        "VALUES (@email, @ccnumber, @flightclass) RETURNING bookingid;";
                    cmd.Parameters.AddWithValue("email", email);
                    cmd.Parameters.AddWithValue("ccnumber", ccNumber);
                    cmd.Parameters.AddWithValue("flightclass", flightClass);
                    var bookingID = (int) cmd.ExecuteScalar();

                    // Insert into the bookingflights table and update booked count
                    foreach (Flight flight in flights)
                    {
                        cmd.CommandText =
                            "INSERT INTO bookingflights (bookingid, date, flightnumber, airlineid) " +
                            "VALUES (@bookingid, @date, @flightnumber, @airlineid)";
                        cmd.Parameters.AddWithValue("bookingid", bookingID);
                        cmd.Parameters.AddWithValue("date", flight.Date);
                        cmd.Parameters.AddWithValue("flightnumber", flight.FlightNumber);
                        cmd.Parameters.AddWithValue("airlineid", flight.AirlineID);
                        cmd.ExecuteNonQuery();

                        if (flightClass == "First")
                        {
                            UpdateFlight(flight.Date, flight.FlightNumber, flight.AirlineID, flight.BookedCoach, flight.BookedFirstClass + 1);
                        }else if (flightClass == "Coach")
                        {
                            UpdateFlight(flight.Date, flight.FlightNumber, flight.AirlineID, flight.BookedCoach + 1, flight.BookedFirstClass);
                        }
                    }

                    InsertMileageProgram(email, bookingID, flights);
                    
                    return bookingID;
                }
            }
        }

        public void InsertFlight(DateTime date, int flightNumber, DateTimeOffset departureTime,
        DateTimeOffset arrivalTime, string departureAirport, string arrivalAirport,
        int maxCoach, int maxFirst, string airlineID, int bookedCoach, int bookedFirst)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "INSERT INTO flight (date, flightnumber, departuretime, arrivaltime, departureairport, maxcoach, maxfirst, airlineid, bookedcoach, bookedfirst) " +
                        "VALUES (@date, @flightnumber, @departuretime, @departureairport, @arrivalairport, @maxcoach, @maxfirst, @airlineid, @bookedcoach, @bookedfirst);";
                    cmd.Parameters.AddWithValue("date", date);
                    cmd.Parameters.AddWithValue("flightnumber", flightNumber);
                    cmd.Parameters.AddWithValue("departuretime", departureTime);
                    cmd.Parameters.AddWithValue("arrivaltime", arrivalTime);
                    cmd.Parameters.AddWithValue("departureairport", departureAirport);
                    cmd.Parameters.AddWithValue("arrivalairport", arrivalAirport);
                    cmd.Parameters.AddWithValue("maxcoach", maxCoach);
                    cmd.Parameters.AddWithValue("maxfirst", maxFirst);
                    cmd.Parameters.AddWithValue("airlineid", airlineID);
                    cmd.Parameters.AddWithValue("bookedcoach", bookedCoach);
                    cmd.Parameters.AddWithValue("bookedfirst", bookedFirst);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void InsertPrice(string flightClass, decimal cost, DateTime date, int flightNumber, string airline)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "INSERT INTO price (flightclass, cost, flightnumber, airlineid) " +
                        "VALUES (@flightclass, @cost, @flightnumber, @airlineid);";
                    cmd.Parameters.AddWithValue("flightclass", flightClass);
                    cmd.Parameters.AddWithValue("cost", cost);
                    cmd.Parameters.AddWithValue("date", date);
                    cmd.Parameters.AddWithValue("flightnumber", flightNumber);
                    cmd.Parameters.AddWithValue("airlineid", airline);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void InsertCreditCardOwner(string email, string ccNumber)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "INSERT INTO creditcardowner (email, ccnumber) " +
                        "VALUES (@email, @ccnumber);";
                    cmd.Parameters.AddWithValue("email", email);
                    cmd.Parameters.AddWithValue("ccnumber", ccNumber);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void InsertLivesAt(string email, int addressID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "INSERT INTO livesat (email, addressid) " +
                        "VALUES (@email, @addressid);";
                    cmd.Parameters.AddWithValue("email", email);
                    cmd.Parameters.AddWithValue("addressid", addressID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void InsertBookingFlights(int bookingID, DateTime date, int flightNumber, string airlineID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "INSERT INTO bookingflights (bookingid, date, flightnumber, airlineid) " +
                        "VALUES (@bookingid, @date, @flightnumber, @airlineid);";
                    cmd.Parameters.AddWithValue("bookingid", bookingID);
                    cmd.Parameters.AddWithValue("date", date);
                    cmd.Parameters.AddWithValue("flightnumber", flightNumber);
                    cmd.Parameters.AddWithValue("airlineid", airlineID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void InsertMileageProgram(string email, int bookingID, IEnumerable<Flight> flights)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    // calculate the number of miles from initial departure airport to final arrival airport

                    foreach (Flight flight in flights)
                    {
                        cmd.Connection = conn;
                        double departureLatitude = 0, departureLongitude = 0, arrivalLatitude = 0, arrivalLongitude = 0;
                        cmd.CommandText =
                            "SELECT latitude, longitude FROM airport WHERE iata_id = @iata_id;";
                        cmd.Parameters.AddWithValue("iata_id", flight.DepartureAirport);
                        using (var reader = cmd.ExecuteReader())
                        {
                            departureLatitude = (reader[reader.GetOrdinal("latitude")] as double?).Value;
                            departureLongitude = (reader[reader.GetOrdinal("longitude")] as double?).Value;
                        }
                        cmd.CommandText =
                            "SELECT latitude, longitude FROM airport WHERE iata_id = @iata_id;";
                        cmd.Parameters.AddWithValue("iata_id", flight.ArrivalAirport);
                        using (var reader = cmd.ExecuteReader())
                        {
                            arrivalLatitude = (reader[reader.GetOrdinal("latitude")] as double?).Value;
                            arrivalLongitude = (reader[reader.GetOrdinal("longitude")] as double?).Value;
                        }

                        var miles = CalculateDistance.Distance(departureLatitude, departureLongitude, arrivalLatitude, arrivalLongitude);
                        
                        cmd.CommandText =
                            "INSERT INTO mileageprogram (miles, email, airlineid, bookingid) " +
                            "VALUES (@miles, @email, @airlineid, @bookingid);";
                        cmd.Parameters.AddWithValue("miles", miles);
                        cmd.Parameters.AddWithValue("email", email);
                        cmd.Parameters.AddWithValue("airlineid", flight.AirlineID);
                        cmd.Parameters.AddWithValue("bookingid", bookingID);
                        cmd.ExecuteNonQuery();
                    }
                }
            }
        }
        #endregion

        #region SQL UPDATES

        public void UpdateCustomer(string firstName, string lastName, string email, string iataID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "UPDATE customer SET firstname = @firstname, lastname = @lastname, iata_id = @iata_id WHERE email = @email;";
                    cmd.Parameters.AddWithValue("email", email);
                    cmd.Parameters.AddWithValue("firstname", firstName);
                    cmd.Parameters.AddWithValue("lastname", lastName);
                    cmd.Parameters.AddWithValue("iata_id", iataID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void UpdateCustomer(string email, string iataID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "UPDATE customer SET iata_id = @iata_id WHERE email = @email;";
                    cmd.Parameters.AddWithValue("email", email);
                    cmd.Parameters.AddWithValue("iata_id", iataID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void UpdateAirport(string iataID, string airportName, string country, string state, double latitude, double longitude)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "UPDATE airport SET airportname = @airportname, country = @country, state = @state, latitude = @latitude, longitude = longitude@ WHERE iata_id = @iata_id;";
                    cmd.Parameters.AddWithValue("airportname", airportName);
                    cmd.Parameters.AddWithValue("country", country);
                    cmd.Parameters.AddWithValue("state", state);
                    cmd.Parameters.AddWithValue("latitude", latitude);
					cmd.Parameters.AddWithValue("longitude", longitude);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void UpdateAddress(int streetNumber, string streetName, string city, string state, string zipCode, string country, int addressID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "UPDATE address SET  streetnumber= @streetnumber, streetname = @streetname, city = @city, state = @state, zipcode = @zipcode, country = @country WHERE addressid = @addressid;";
                    cmd.Parameters.AddWithValue("streetnumber", streetNumber);
                    cmd.Parameters.AddWithValue("streetname", streetName);
                    cmd.Parameters.AddWithValue("city", city);
                    cmd.Parameters.AddWithValue("state", state);
					cmd.Parameters.AddWithValue("zipcode", zipCode);
                    cmd.Parameters.AddWithValue("country", country);
                    cmd.Parameters.AddWithValue("addressid", addressID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void UpdateCreditCard(string type, string ccNumber, string cardFirstName, string cardLastName, DateTime expirationDate,
            string cvc, int addressID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "UPDATE creditcard SET type = @type, cardfirstname = @cardfirstname, cardlastname = @cardlastname, expirationdate = @expirationdate, cvc = @cvc, addressid = @addressid WHERE ccnumber = @ccnumber;";
                    cmd.Parameters.AddWithValue("type", type);
                    cmd.Parameters.AddWithValue("cardfirstname", cardFirstName);
                    cmd.Parameters.AddWithValue("cardlastname", cardLastName);
                    cmd.Parameters.AddWithValue("expirationdate", expirationDate.Date);
					cmd.Parameters.AddWithValue("cvc", cvc);
                    cmd.Parameters.AddWithValue("addressid", addressID);
                    cmd.Parameters.AddWithValue("ccnumber", ccNumber);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void UpdateAirline(string airlineID, string country, string airlineName)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "UPDATE airline SET country = @country, airlinename = @airlinename WHERE airlineid = @airlineid;";
                    cmd.Parameters.AddWithValue("airlineid", airlineID);
                    cmd.Parameters.AddWithValue("country", country);
                    cmd.Parameters.AddWithValue("airlinename", airlineName);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void UpdateBooking(int bookingID, string email, string ccNumber, string flightClass)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "UPDATE booking SET email = @email, ccnumber = @ccnumber, flightclass = @flightclass WHERE bookingid = @bookingid;";
                    cmd.Parameters.AddWithValue("bookingid", bookingID);
                    cmd.Parameters.AddWithValue("email", email);
                    cmd.Parameters.AddWithValue("ccnumber", ccNumber);
                    cmd.Parameters.AddWithValue("flightclass", flightClass);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void UpdateFlight(DateTime date, int flightNumber, string airlineID, int bookedCoach, int bookedFirst)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "UPDATE flight SET bookedcoach = @bookedcoach, bookedfirst = @bookedfirst" +
                        "WHERE date = @date AND flightnumber = @flightnumber AND airlineid = @airlineid;";
                    cmd.Parameters.AddWithValue("date", date);
                    cmd.Parameters.AddWithValue("flightnumber", flightNumber);
					cmd.Parameters.AddWithValue("airlineid", airlineID);
                    cmd.Parameters.AddWithValue("bookedcoach", bookedCoach);
                    cmd.Parameters.AddWithValue("bookedfirst", bookedFirst);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void UpdatePrice(string flightClass, decimal cost, DateTime date, int flightNumber, string airline)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "UPDATE price SET cost = @cost WHERE flightclass = @flightclass AND date = @date AND flightnumber = @flightnumber AND airlineid = @airlineid;";
                    cmd.Parameters.AddWithValue("flightclass", flightClass);
                    cmd.Parameters.AddWithValue("cost", cost);
                    cmd.Parameters.AddWithValue("date", date);
                    cmd.Parameters.AddWithValue("flightnumber", flightNumber);
					cmd.Parameters.AddWithValue("airlineid", airline);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void UpdateMileageProgram(int miles, string email, string airline, int bookingID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "UPDATE mileageprogram SET miles = @miles WHERE email = @email AND airlineid = @airlineid AND bookingid = @bookingid;";
                    cmd.Parameters.AddWithValue("miles", miles);
                    cmd.Parameters.AddWithValue("email", email);
                    cmd.Parameters.AddWithValue("airlineid", airline);
                    cmd.Parameters.AddWithValue("bookingid", bookingID);
                    cmd.ExecuteNonQuery();
                }
            }
        }
        #endregion

        #region SQL DELETES

        public void DeleteCustomer(string email)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "DELETE FROM customer WHERE email = @email;";
                    cmd.Parameters.AddWithValue("email", email);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void DeleteAirport(string iataID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "DELETE FROM airport WHERE iata_id = @iata_id;";
                    cmd.Parameters.AddWithValue("iata_id", iataID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void DeleteAddress(int addressID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "DELETE FROM address WHERE addressid = @addressid;";
                    cmd.Parameters.AddWithValue("addressid", addressID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void DeleteCreditCard(string ccNumber)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "DELETE FROM creditcard WHERE ccnumber = @ccnumber;";
                    cmd.Parameters.AddWithValue("ccnumber", ccNumber);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void DeleteCreditCard(string email, string ccNumber)
        {
            DeleteCreditCard(ccNumber);
            DeleteCreditCardOwner(email, ccNumber);
        }

        public void DeleteAirline(string airlineID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "DELETE FROM airline WHERE airlineid = @airlineid;";
                    cmd.Parameters.AddWithValue("airlineid", airlineID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void DeleteBooking(int bookingID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "DELETE FROM booking WHERE bookingid = @bookingid;";
                    cmd.Parameters.AddWithValue("bookingid", bookingID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void DeleteFlight(DateTime date, int flightNumber, string airlineID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "DELETE FROM flight WHERE date = @date AND flightnumber = @flightnumber AND airlineid = @airlineid;";
                    cmd.Parameters.AddWithValue("date", date);
					cmd.Parameters.AddWithValue("flightnumber", flightNumber);
					cmd.Parameters.AddWithValue("airlineid", airlineID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void DeletePrice(string flightClass, DateTime date, int flightNumber, string airline)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "DELETE FROM price WHERE flightclass = @flightclass AND date = @date AND flightnumber = @flightnumber AND airlineid = @airlineid;";
                    cmd.Parameters.AddWithValue("flightclass", flightClass);
					cmd.Parameters.AddWithValue("date", date);
					cmd.Parameters.AddWithValue("flightnumber", flightNumber);
					cmd.Parameters.AddWithValue("airlineid", airline);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void DeleteCreditCardOwner(string email, string ccNumber)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "DELETE FROM creditcardowner WHERE email = @email AND ccnumber = @ccnumber;";
                    cmd.Parameters.AddWithValue("email", email);
					cmd.Parameters.AddWithValue("ccnumber", ccNumber);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void DeleteLivesAt(string email, int addressID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "DELETE FROM livesat WHERE email = @email AND addressid = @addressid AND  = @ AND  = @;";
                    cmd.Parameters.AddWithValue("email", email);
					cmd.Parameters.AddWithValue("addressid", addressID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void DeleteBookingFlights(int bookingID, DateTime date, int FlightNumber, string airlineID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "DELETE FROM bookingflights WHERE bookingid = @bookingid AND date = @date AND flightnumber = @flightnumber AND airlineid = @airlineid;";
                    cmd.Parameters.AddWithValue("bookingid", bookingID);
					cmd.Parameters.AddWithValue("date", date);
					cmd.Parameters.AddWithValue("flightnumber", FlightNumber);
					cmd.Parameters.AddWithValue("airlineid", airlineID);
                    cmd.ExecuteNonQuery();
                }
            }
        }

        public void DeleteMileageProgram(string email, string airlineID, int bookingID)
        {
            using (var conn = new NpgsqlConnection(_connString))
            {
                conn.Open();

                using (var cmd = new NpgsqlCommand())
                {
                    cmd.Connection = conn;
                    cmd.CommandText =
                        "DELETE FROM mileageprogram WHERE email = @email AND airlineid = @airlineid AND bookingid = @bookingid;";
                    cmd.Parameters.AddWithValue("email", email);
					cmd.Parameters.AddWithValue("airlineid", airlineID);
					cmd.Parameters.AddWithValue("bookingid", bookingID);
                    cmd.ExecuteNonQuery();
                }
            }
        }
        #endregion
     }
}