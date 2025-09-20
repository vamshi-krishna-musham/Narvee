import { commonerror } from "./common-error";

export class unhandled extends commonerror {
    constructor(message: string){
        super(message);
    }
}