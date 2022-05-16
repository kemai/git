package uk.ac.leedsbeckett.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.ac.leedsbeckett.student.model.*;
import uk.ac.leedsbeckett.student.service.EnrolmentService;
import uk.ac.leedsbeckett.student.service.IntegrationService;


import java.util.Set;




@Configuration
public class MiscellaneousBeans {


    @Bean
    CommandLineRunner initDatabase(CourseRepository courseRepository, StudentRepository studentRepository, IntegrationService integrationService, EnrolmentService enrolmentService){
        return args -> {

            Account account;

            //Course Creation
            Course sesc = new Course();
            sesc.setTitle("SESC");
            sesc.setDescription("Software Engineering for Service Computing");
            sesc.setFee(150.00);

            Course ntwk = new Course();
            ntwk.setTitle("NM");
            ntwk.setDescription("Network Management");
            ntwk.setFee(50.00);

            Course pm = new Course();
            pm.setTitle("PM");
            pm.setDescription("Project Management");
            pm.setFee(60.00);

            Course hst = new Course();
            hst.setTitle("HST");
            hst.setDescription("History");
            hst.setFee(35.00);

            Course ai = new Course();
            ai.setTitle("AI");
            ai.setDescription("Artificial Intelligence");
            ai.setFee(55.00);

            Course am = new Course();
            am.setTitle("AM");
            am.setDescription("Agile Methodology");
            am.setFee(20.00);

         //   courseRepository.saveAllAndFlush(Set.of(sesc,ntwk,pm,hst,ai,am));
            
            // Student creation


            Student abba = new Student();
            abba.setSurname("Abba");
            abba.setFirstname("Gale");
            abba.setExternalStudentId("c9999");
          //  abba.enrolInCourse(ntwk);
          //  abba.enrolInCourse(ai);


            Student marco = new Student();
            marco.setSurname("Brescia");
            marco.setFirstname("Marco");
            marco.setExternalStudentId("c2222");
           // marco.enrolInCourse(sesc);
           // marco.enrolInCourse(am);

            Student pedro = new Student();
            pedro.setSurname("Alvarez");
            pedro.setFirstname("Pedro");
            pedro.setExternalStudentId("c3333");
           // pedro.enrolInCourse(pm);
          //  pedro.enrolInCourse(hst);

            //studentRepository.saveAllAndFlush(Set.of(abba,marco,pedro));

            enrolmentService.createNewAccount(marco);
            enrolmentService.createNewAccount(pedro);
            enrolmentService.createNewAccount(abba);



            enrolmentService.enrolStudentInCourse(marco,sesc);
            enrolmentService.enrolStudentInCourse(marco,ai);
            enrolmentService.enrolStudentInCourse(marco,ntwk);
            enrolmentService.enrolStudentInCourse(marco,am);

            enrolmentService.enrolStudentInCourse(pedro,pm);
            enrolmentService.enrolStudentInCourse(pedro,hst);

            enrolmentService.enrolStudentInCourse(abba,ai);
            enrolmentService.enrolStudentInCourse(abba,am);



         account =  integrationService.getStudentAccount(marco.getExternalStudentId());
         Account account2 = integrationService.getStudentAccount(pedro.getExternalStudentId());
         Account  account3 =  integrationService.getStudentAccount(abba.getExternalStudentId());

            System.out.println("The Account student requested has student ID" + account.getStudentId());
            System.out.println("The Account student requested has student ID" + account2.getStudentId());
            System.out.println("The Account student requested has student ID" + account3.getStudentId());



        };

    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }
}
