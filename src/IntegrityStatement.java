
public final class IntegrityStatement {
    public static String signature() {
    	/*
         * Signing this statement means that you have read and understood the following statement:
         *
         * "I certify that the work I have submitted is entirely my own. I have not received any
         *  part of it from any other person, nor have I given any part of it to others for their use. I have not copied any
         *  part of my answers from any other source, and what I submit is my own creation. I understand that formal
         *  proceedings will be taken against me before the BGU Disciplinary Committee if there is any suspicion that my
         *  work contains code/answers that is not my own".
         */
    	
        String sign = "Hila Zuckerman (212735450)"; // <- Fill in your name and ID here! For example: "Israel Israeli (123456789)"
        if (sign.length() == 0) {
            throw new UnsupportedOperationException("You didn't sign the integrity statement!");
        }
        return sign;
        
        /*
         *  If you have relied on or used an external source such as shared file drivers,
         *  Large Language Models (LLMs) including ChatGPT, forums, websites, books, etc.
         *  state those here:
         *
         *  < FILL IN YOUR SOURCES >
         */
    }
}
