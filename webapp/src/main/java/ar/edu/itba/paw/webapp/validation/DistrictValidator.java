package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.District;

import javax.validation.ConstraintValidator;

public class DistrictValidator implements ConstraintValidator<SpecifiedDistrict, District> {

    @Override
    public void initialize(SpecifiedDistrict specifiedDistrict) {}

    @Override
    public boolean isValid(District district, javax.validation.ConstraintValidatorContext context) {
        if (district == null) {
            return true;
        }
        return district != District.NOT_SPECIFIED;
    }
}
