import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities

driver = {
    DesiredCapabilities capabilities = DesiredCapabilities.chrome()

    capabilities.setCapability(ChromeOptions.CAPABILITY, new ChromeOptions()
            .addArguments("headless")
    )

    return new ChromeDriver(capabilities)
}