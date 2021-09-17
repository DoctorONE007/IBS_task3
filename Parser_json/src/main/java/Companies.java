import java.util.List;

public class Companies {
        List<Company> companies;
    }
    class Company{
        int id;
        String name;
        String address;
        String phoneNumber;
        String INN;
        String founded;
        List<Securities> securities;
    }
    class Securities{
        String name;
        List<String> currency;
        String code;
        String date;
    }

