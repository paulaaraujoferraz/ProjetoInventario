package br.com.inventario.domain;


public class WrapData {
    private String url;
    private String method;
    private String name;
    private String email;
    private String nrnff;

    private int age;
    private Image image;


    public WrapData() {
        this.image = new Image();
    }

    public WrapData(String url, String method, String name, String email, String nrnff, int age, Image image) {
        this.url = url;
        this.method = method;
        this.name = name;
        this.email = email;
        this.age = age;
        this.nrnff = nrnff;
        this.image = image;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNrnff() {
        return nrnff;
    }

    public void setNrnff(String nrnff) {
        this.nrnff = nrnff;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}