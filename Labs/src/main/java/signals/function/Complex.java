package signals.function;

public class Complex {
    public double real;
    public double imag;
    public static Complex ZERO() {
        return new Complex(0, 0);
    }

    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public static Complex fromPlane(double modulus, double argument) {
        return new Complex(modulus*Math.cos(argument), modulus*Math.sin(argument));
    }

    public Complex add(Complex other) {
        return new Complex(this.real + other.real, this.imag + other.imag);
    }
    public Complex subtract(Complex other) {
        return new Complex(this.real - other.real, this.imag - other.imag);
    }

    public Complex multiply(Complex other) {
        return new Complex(
                this.real * other.real - this.imag * other.imag,
                this.real * other.imag + this.imag * other.real
        );
    }

    public Complex divide(double other) {
        if (other == 0) {
            throw new ArithmeticException("Деление на ноль недопустимо.");
        }
        return new Complex(this.real / other, this.imag / other);
    }

    public void selfDivide(double other) {
        if (other == 0) {
            throw new ArithmeticException("Деление на ноль недопустимо.");
        }
        real /= other; this.imag /= other;
    }

    public double abs() {
        return Math.sqrt(real * real + imag * imag);
    }

    public Complex conjugate() {
        return new Complex(real , -imag);
    }

    public void selfConjugate() {
        imag = -imag;
    }

    @Override
    public String toString() {
        return "(" + real + "," + imag + ")";
    }
}