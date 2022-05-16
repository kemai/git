package uk.ac.leedsbeckett.student.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.leedsbeckett.student.model.Account;
import uk.ac.leedsbeckett.student.model.Invoice;

import java.nio.channels.AcceptPendingException;

@Component
public class IntegrationService {

    private final RestTemplate restTemplate;
    private String url_student = "http://localhost:8081/accounts/student/";
    private String url_invoice = "http://localhost:8081/invoices/";

    private String url_account = "http://localhost:8081/accounts/";


    public IntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Account getStudentAccount(String studentId){
        return restTemplate.getForObject(url_student + studentId, Account.class);
    }

    public Account createStudentAccount(Account account){
        return restTemplate.postForObject(url_account,account,Account.class);

    }

    public Invoice createCourseFeeInvoice(Invoice invoice){
        return restTemplate.postForObject(url_invoice, invoice, Invoice.class);
    }

}
