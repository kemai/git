package uk.ac.leedsbeckett.student.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.leedsbeckett.student.controller.StudentController;
import uk.ac.leedsbeckett.student.model.Student;
import uk.ac.leedsbeckett.student.model.StudentModelAssembler;
import uk.ac.leedsbeckett.student.model.StudentRepository;
import org.springframework.hateoas.EntityModel;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentModelAssembler studentModelAssembler;


    public StudentService(StudentRepository studentRepository, StudentModelAssembler studentModelAssembler) {
        this.studentRepository = studentRepository;
        this.studentModelAssembler = studentModelAssembler;
    }

    public EntityModel<Student> getStudentByIdJson(long id){
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course with id" +id + "not found"));
                return EntityModel.of(student,
                        linkTo(methodOn(StudentController.class)
                                .getStudentJson(student.getId())).withSelfRel());

    }

    public ModelAndView getStudentbyId(Long id){
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject(studentRepository.findById(id).orElseThrow(RuntimeException::new));
        return modelAndView;
    }

    public CollectionModel<EntityModel<Student>> getAllStudentsJson(){
        List<EntityModel<Student>> studentList = studentRepository.findAll()
                .stream()
                .map(studentModelAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(studentList,
                linkTo(methodOn(StudentController.class).getStudentsJson()).withSelfRel());
    }


    public ResponseEntity<EntityModel<Student>> createNewStudentJson(Student newStudent){
        if(newStudent.getExternalStudentId() == null || newStudent.getExternalStudentId().isEmpty()){
            throw new RuntimeException("Missing student ID");
        }
        Student savedStudent;
        try{
            savedStudent = studentRepository.save(newStudent);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("A student with the same student ID" + newStudent.getExternalStudentId());
        }

        EntityModel<Student> entityModel = studentModelAssembler.toModel(savedStudent);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    public ResponseEntity<EntityModel<Student>> updateStudentJson(Long id, Student newStudent){
       Student existingStudent = studentRepository.findById(id).orElseThrow(RuntimeException::new);
        existingStudent.setFirstname(newStudent.getFirstname());
        existingStudent.setSurname(newStudent.getSurname());
        existingStudent.setExternalStudentId(newStudent.getExternalStudentId());
        studentRepository.save(existingStudent);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(studentModelAssembler.toModel(existingStudent));
    }

    public ResponseEntity<?> deleteAccount(Long id) {
        Student account = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not exits"));
        studentRepository.delete(account);
        return ResponseEntity.noContent().build();
    }

    public String showPortal(Model model) {
        Student student = new Student();
        model.addAttribute("student", student);
        return "portal";
    }


}
