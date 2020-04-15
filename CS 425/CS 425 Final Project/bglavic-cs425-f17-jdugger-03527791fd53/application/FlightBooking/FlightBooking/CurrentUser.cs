using System;

namespace FlightBooking
{
    public class CurrentUser
    {
        private static string email;

        private CurrentUser() { }

        public static string Email
        {
            get { return email; }
            set
            {
                if (!string.IsNullOrWhiteSpace(Email))
                {
                    throw new Exception();
                }
                email = value;
            }
        }
    }
}