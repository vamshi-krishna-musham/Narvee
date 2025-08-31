import { commonerror } from './common-error';

export class HandledError extends commonerror{
    constructor(message: string){
        super(message);
    }
}