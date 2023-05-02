package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.pages.DashboardPage;
import ru.netology.pages.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;

public class TransferTest {
    LoginPage loginPage;
    DashboardPage dashboardPage;

    @BeforeEach
    void setup() {
        loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);
    }


    @Test
    public void shouldTransferFromFirstToSecond() {
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance=dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance=dashboardPage.getCardBalance(secondCardInfo);
        var amount=generateValidAmount(firstCardBalance);
        var expectedFirstCardBalance=firstCardBalance-amount;
        var expectedSecondCardBalance=secondCardBalance+amount;
        var transferPage=dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage=transferPage.makeValidTransfer(String.valueOf(amount),firstCardInfo);
        var actualFirstCardBalance=dashboardPage.getCardBalance(firstCardInfo);
        var actualSecondCardBalance=dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(expectedFirstCardBalance,actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance,actualSecondCardBalance);
    }
    @Test
    public void shouldHaveErrorMessageIfAmountMoreThanBalance() {
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance=dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance=dashboardPage.getCardBalance(secondCardInfo);
        var amount=generateInvalidAmount(secondCardBalance);
        var transferPage=dashboardPage.selectCardToTransfer(firstCardInfo);
        transferPage.makeTransfer(String.valueOf(amount),secondCardInfo);
        transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
        var actualFirstCardBalance=dashboardPage.getCardBalance(firstCardInfo);
        var actualSecondCardBalance=dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(firstCardBalance,actualFirstCardBalance);
        assertEquals(secondCardBalance,actualSecondCardBalance);
    }

}
