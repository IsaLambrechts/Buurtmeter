package be.ap.edu.buurtmeter;

public class Data {
    private String name;
    private Integer amount;
    private Boolean checked;

    public Data(String name, Integer amount, Boolean checked) {
        this.name = name;
        this.amount = amount;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
