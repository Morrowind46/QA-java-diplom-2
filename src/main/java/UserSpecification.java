import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class UserSpecification {

    public static RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setBaseUri("https://stellarburgers.nomoreparties.site/")
            .build();
        }
}
