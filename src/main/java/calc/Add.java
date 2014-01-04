
package calc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="d1" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="d2" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "d1",
    "d2"
})
@XmlRootElement(name = "add")
public class Add {

    protected Double d1;
    protected Double d2;

    /**
     * Gets the value of the d1 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getD1() {
        return d1;
    }

    /**
     * Sets the value of the d1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setD1(Double value) {
        this.d1 = value;
    }

    /**
     * Gets the value of the d2 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getD2() {
        return d2;
    }

    /**
     * Sets the value of the d2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setD2(Double value) {
        this.d2 = value;
    }

}
