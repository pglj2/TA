package ta

class Evaluation {
    String origin;
    String value;
    Date applicationDate;
    String criterion;
    static constraints = {
        origin inList :["Test","Mini-Test","Form","Final","--"], blank: false
        value inList :["MA","MPA","MANA","--"], blank :false
        criterion nullable : false, blank : false
    }

    public Evaluation(String origin, String value, Date applicationDate, String criterion){
        this.origin = origin;
        this.value = value;
        this.applicationDate = applicationDate;
        this.criterion = criterion;
    }

    public boolean compatibleTo(Evaluation evaluationInstance){
        if(this.origin.equals(evaluationInstance.getOrigin()) && this.value.equals(evaluationInstance.getValue()) && this.applicationDate.compareTo(evaluationInstance.getApplicationDate())==0 && this.criterion.equals(evaluationInstance.getCriterion()))
        {
            return true
        }else {
            return false
        }
    }
    public boolean compatibleToNoValue(Evaluation evaluationInstance){
        if(this.origin.equals(evaluationInstance.getOrigin()) && this.applicatinoDate.compareTo(evaluationInstance.getApplicationDate())==0 && this.criterion.equals(evaluationInstance.getCriterion()))
        {
            return true
        }else {
            return false
        }
    }
}
