
package demo;

import mylib.*;

public class program {

public static void main(String args[])
{
   String privatedata = Security.getPrivateData();

   System.out.println(privatedata);
}

}
