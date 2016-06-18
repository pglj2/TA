package ta

import org.apache.ivy.core.settings.Validatable

import java.text.SimpleDateFormat

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class StudentController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    public static Date formattedDate(String dateInString){
        def formatter = new SimpleDateFormat("dd/mm/yyyy");
        Date date = formatter.parse(dateInString);
        return date;
    }
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Student.list(params), model:[studentInstanceCount: Student.count()]
    }
    public boolean addEvaluationsToAllStudents(LinkedList<Evaluation> evaluationList){
        //List<Student> students = Student.list();
        for(int i = 0; i < Student.list().size(); i++){
            Student.list().get(i).addEvaluation(evaluationList.get(i))
            Student.list().get(i).save (
                flush: true,
                failOnError: true
            )
        }
        return true
    }
    public boolean addEvaluationToAllStudentes(){
        def evaluationInstance = new Evaluation(params);
        for(Student student : Student.findAll()){
            student.addEvaluation(evaluationInstance);
            student.save flush : true
        }
        return true
    }
    public void addEvaluationToStudent(String login){
        def student = Student.findByLogin(login);
        def evaluationInstance = new Evaluation(params);
        student.addEvaluation(evaluationInstance);
        student.save flush : true
    }

    public void addEvaluationToStudent2(String login, Date applicationDate){
        def student = Student.findByLogin(login)
        def eval = Evaluation.findByApplicationDate(applicationDate)
        student.addEvaluation(eval)
        student.save flush: true
    }


    public void addCriterionToAllStudent(String description){
        def students = Student.findAll();
        for(int i =0; i<students.size();i++){
            def evCriterion = new EvaluationsByCriterion(Criterion.findByDescription(description));
            Student student = students.get(i);
            student.addEvaluationsByCriterion(evCriterion)
        }
    }

    public List<Evaluation> countStudentsEvaluated(String criterionName, String origin, String dateInString){
        List<Evaluation> returningValue;
        def evaluation = new Evaluation(origin,null,this.formattedDate(dateInString),new Criterion(criterionName));
        def students = Student.findAll();
        for(int i =0; i< students.size();i++){
            returningValue.add(students.get(i).findEvaluationByCriterion(evaluation.getCriterion().getDescription()).findSpecificEvaluation(evaluation))
        }
        return returningValue;
    }

    public boolean checkRedundantEvaluationAllStudents(String criterionName,String origin,String dateInString){
        def evaluation = new Evaluation(origin,null,this.formattedDate(dateInString),new Criterion(criterionName));
        List<Student> students = Student.findAll();
        for(int i=0; i<students.size();i++){
            def evCriterion = students.get(i).findEvaluationByCriterion(criterionName);
            if(evCriterion.findAll{it -> evCriterion.findSpecificEvaluation(evaluation)!= null}.size()>1){
                return false
            }
        }
        return true
    }

    public boolean checkEvaluationsAllStudents(String criterionName, String origin, String dateInString){
       def evaluation = new Evaluation(origin,null,this.formattedDate(dateInString),new Criterion(criterionName));
       List<Student> students = Student.findAll()
       for(int i =0; i<students.size();i++){
           def evCriterion  = students.get(i).findEvaluationByCriterion(criterionName);
           if(evCriterion.findSpecificEvaluation(evaluation) != null){
               return true;
           }else{
               return false
           }
       }
    }
    public int countAllStudents(){
        return Student.findAll().size();
    }

    /* COMENTADO POR RODRIGO CALEGARIO 28/05/16
    public boolean saveStudent(Student student){
        if(Student.findByLogin(student.login) ==null){
            student.save flush: true
            return true
        }else{
            return false
        }
    }
    */

    public boolean saveStudent() {
        def studentInstance = new Student(params);
        if(Student.findByLogin(studentInstance.login) == null) {
            studentInstance.save flush: true
            return true
        }
        return false
    }

    public Student createAndSaveStudent(){
        Student student = new Student(params)
        if(Student.findByLogin(student.getLogin()) == null) {
            student.save flush: true
        }
        return student
    }

    def addEvaluation(Student studentInstance, Evaluation evaluationInstance){
        def student = studentInstance;
        student.addEvaluation(evaluationInstance);
        student.save flush : true
    }

/*    def addCriterion(Criterion criterionInstance){
        for(Student student : Student.findAll()){
            student.criterionsAndEvaluations.add(criterionInstance)
            save(student)
        }
    }
*/
    def show(Student studentInstance) {
        List<Student> l = Student.list()
        Student s = Student.findByLogin(studentInstance.login)
        respond s
    }

    def create() {
        respond new Student(params)
    }

    def search(){
        respond view: 'search'
    }
    @Transactional
    def save(Student studentInstance) {
        if (studentInstance == null) {
            notFound()
            return
        }

        if (studentInstance.hasErrors()) {
            respond studentInstance.errors, view:'create'
            return
        }

        studentInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'student.label', default: 'Student'), studentInstance.id])
                redirect studentInstance
            }
            '*' { respond studentInstance, [status: CREATED] }
        }
    }

    def edit(Student studentInstance) {
        respond studentInstance
    }

    @Transactional
    def update(Student studentInstance) {
        if (studentInstance == null) {
            notFound()
            return
        }

        if (studentInstance.hasErrors()) {
            respond studentInstance.errors, view:'edit'
            return
        }

        studentInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'student.label', default: 'Student'), studentInstance.id])
                redirect studentInstance
            }
            '*'{ respond studentInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Student studentInstance) {

        if (studentInstance == null) {
            notFound()
            return
        }

        studentInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'student.label', default: 'Student'), studentInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'student.label', default: 'Student'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    public Student searchStudent (){
        def student = Student.findByLogin(params)
        return student
    }

    public void groupSave(List<Student> group){
        for(int i=0; i<group.size(); i++){
            group.get(i).save flush: true;
        }
    }

    def saveGroup(){
        String group = params.name
        String[] students = group.split(";")
        for (int i = 0; i < students.size(); i++){
            List<String> token1 = students[i].tokenize(':')
            String info = token1.get(0)
            List<String> token2 = info.tokenize('(')
            String name = token2.get(0)
            String login = token2.get(1)
            Student novo = new Student(name, login)
            novo.calcMedia()

            if(Student.findByLogin(novo.getLogin()) == null) {
                novo.save flush: true
            }
        }
        flash.message = message(code: 'default.created.message', args: [message(code: students.length, 'student.label', default: 'Student')])
        redirect action:"index", method:"GET"
    }

    def createGroup(){
        respond view: 'createGroup'
    }

}
