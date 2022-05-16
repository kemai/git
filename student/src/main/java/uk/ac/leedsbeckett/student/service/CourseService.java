package uk.ac.leedsbeckett.student.service;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import uk.ac.leedsbeckett.student.controller.CourseController;
import uk.ac.leedsbeckett.student.model.Course;
import uk.ac.leedsbeckett.student.model.CourseModelAssembler;
import uk.ac.leedsbeckett.student.model.CourseRepository;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseModelAssembler courseModelAssembler;

    public CourseService(CourseRepository courseRepository, CourseModelAssembler courseModelAssembler){
        this.courseRepository = courseRepository;
        this.courseModelAssembler = courseModelAssembler;
    }

    public List<Course> getAllCourses(){
       return courseRepository.findAll();
    }

    public CollectionModel<EntityModel<Course>> getAllCoursesJson(){
        List<EntityModel<Course>> coursList = courseRepository.findAll()
                .stream()
                .map(courseModelAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(coursList, linkTo(methodOn(CourseController.class)
                .getCoursesJson())
                .withSelfRel());
    }

    public EntityModel<Course> getCourseByIdJson(Long id){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course with id" + id + " not found"));
        return courseModelAssembler.toModel(course);


    }

    public ResponseEntity<EntityModel<Course>> createNewCourseJson(Course newCourse){
        Course savedCourse = courseRepository.save(newCourse);

        EntityModel<Course> entityModel = courseModelAssembler.toModel(savedCourse);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    public ResponseEntity<EntityModel<Course>> updateCourseJson(Long id, Course newCourse){
        Course existingCourse = courseRepository.findById(id).orElseThrow(RuntimeException::new);
        existingCourse.setTitle(newCourse.getTitle());
        existingCourse.setDescription(newCourse.getDescription());
        existingCourse.setFee(newCourse.getFee());
        courseRepository.save(existingCourse);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseModelAssembler.toModel(existingCourse));
    }

    public EntityModel<Course> getCourseByTitle(String title) {
        Course course = courseRepository.findCourseByTitle(title);
        if (course == null) {
            throw new RuntimeException("Error");
        }
        return courseModelAssembler.toModel(course);
    }

    public String findCourseThroughPortal(Course course, BindingResult bindingResult, Model model) {
        if (course == null || course.getId() == null) {
            throw new RuntimeException("Error");
        }
        if (bindingResult.hasErrors()) {
            return "portal";
        }
        Course found = getCourseByTitle(course.getTitle()).getContent();
        model.addAttribute("invoice", found);
        return "invoice";
    }


}

