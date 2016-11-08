/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUIComponents;

/**
 *
 * @author Jonas
 */

public class Complex {
    private final double _real;   
    private final double _imag;   
    
    /*
     * @Constructor
     * create new complex object
     */
    public Complex(double real, double imag) {
        _real = real;
        _imag = imag;
    }
    
    /*
     * @Function
     * return vector absolute value
     */
    public double abs()   { 
        return Math.hypot(_real, _imag); 
    } 
    /*
     * @Function
     * return vector angular argument
     */
    public double phase() { 
        return Math.atan(_imag/_real);
    }  

    /*
     * @Function
     * return vector addition
     */
    public Complex plus(Complex b) {
        Complex a = this;             // invoking object
        double real = a._real + b._real;
        double imag = a._imag + b._imag;
        return new Complex(real, imag);
    }
    
    /*
     * @Function
     * return vector subraction
     */
    public Complex minus(Complex b) {
        Complex a = this;
        double real = a._real - b._real;
        double imag = a._imag - b._imag;
        return new Complex(real, imag);
    }

    /*
     * @Function
     * return vector product
     */
    public Complex times(Complex b) {
        Complex a = this;
        double real = a._real * b._real - a._imag * b._imag;
        double imag = a._real * b._imag + a._imag * b._real;
        return new Complex(real, imag);
    }
    
    /*
     * @Function
     * return scalar product
     */
    public Complex times(double alpha) {
        return new Complex(alpha * _real, alpha * _imag);
    }
    
    /*
     * @Function
     * return a new Complex object whose value is the conjugate of this
     */
    public Complex conjugate() {  
        return new Complex(_real, -_imag); 
    }

    /*
     * @Function
     * return the reciprocal of this
     */
    public Complex reciprocal() {
        double scale = _real*_real + _imag*_imag;
        return new Complex(_real / scale, -_imag / scale);
    }

    /*
     * @Function
     * return the real part
     */
    public double re() { return _real; }
    
    /*
     * @Function
     * return the imaginary part
     */
    public double im() { return _imag; }

    /*
     * @Function
     * return a / b
     */
    public Complex divides(Complex b) {
        Complex a = this;
        return a.times(b.reciprocal());
    }

    /*
     * @Function
     * return a new Complex object whose value is the complex exponential of this
     */
    public Complex exp() {
        return new Complex(Math.exp(_real) * Math.cos(_imag), Math.exp(_real) * Math.sin(_imag));
    }
    
    /*
    * @Function
    * return a new Complex object whose value is the complex sine of this
    */
    public Complex sin() {
        return new Complex(Math.sin(_real) * Math.cosh(_imag), Math.cos(_real) * Math.sinh(_imag));
    }

    /*
     * @Function
     * return a new Complex object whose value is the complex cosine of this
     */
    public Complex cos() {
        return new Complex(Math.cos(_real) * Math.cosh(_imag), -Math.sin(_real) * Math.sinh(_imag));
    }
    
    /*
     * @Function
     * return a new Complex object whose value is the complex tangent of this
     */
    public Complex tan() {
        return sin().divides(cos());
    }
    
    /*
     * @Function
     * a static version of plus
     */
    public static Complex plus(Complex a, Complex b) {
        double real = a._real + b._real;
        double imag = a._imag + b._imag;
        Complex sum = new Complex(real, imag);
        return sum;
    }
}