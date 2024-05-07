
export const errorMessages: { [key: number]: string } = {
    0: $localize`Unknown Error`,
    400: $localize`Bad Request`,
    401: $localize`Unauthorized`,
    403: $localize`Forbidden`,
    404: $localize`Not Found`,
    500: $localize`Internal Server Error`,
    503: $localize`Service Unavailable`,
    504: $localize`Gateway Timeout`,
}

export interface PriceType extends genericEnum { }

export interface OfferingCategory extends genericEnum { }

export interface District extends genericEnum { }

interface genericEnum {
    name: string;
    value: string;
}