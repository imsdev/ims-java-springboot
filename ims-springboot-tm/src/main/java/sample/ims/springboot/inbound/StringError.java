package sample.ims.springboot.inbound;

public class StringError {
    private String errorMsg;
    
    public StringError(String errorMsg){
        this.errorMsg = errorMsg;
    }
 
    public String getErrorMessage() {
        return errorMsg;
    }
}
