using System.ComponentModel;

namespace FlightBooking.ViewModels
{
    public class LoginViewModel
    {
        public string Email { get; set; }
        [DisplayName("First Name")]
        public string FirstName { get; set; }
        [DisplayName("Last Name")]
        public string LastName { get; set; }
    }
}