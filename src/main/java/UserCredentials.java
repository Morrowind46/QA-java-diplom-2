import org.apache.commons.lang3.RandomStringUtils;

public class UserCredentials {

    public String email;
    public String password;

    public UserCredentials () {

    }
    public UserCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Паттенр фабричный метод: (это метод, который возвращает какой-то объект)
    public static UserCredentials from(User user) {
        return new UserCredentials(user.email, user.password);
    }

    public UserCredentials setEmail (String email){
        this.email = email;
        return this;
    }

    public UserCredentials setPassword (String password) {
        this.password = password;
        return this;
    }

    public static UserCredentials getCredentialsWithEmailOnly (User user) {
        return new UserCredentials().setEmail(user.email);
    }
    public static UserCredentials getCredentialsWithPasswordOnly (User user) {
        return new UserCredentials().setPassword(user.password);
    }

    public static UserCredentials getCredentialsWithRandomEmail (User user) {
        return new UserCredentials(RandomStringUtils.randomAlphabetic(6) + "@gmail.com", user.password);
    }

    public static UserCredentials getCredentialsWithRandomPassword (User user) {
        return new UserCredentials(user.email, RandomStringUtils.randomAlphabetic(6));
    }
}
