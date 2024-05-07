// meta-reducers.ts
import { ActionReducer } from '@ngrx/store';
import { UserLogoutActions } from './user/user.actions';

export function logoutMetaReducer(reducer: ActionReducer<any>): ActionReducer<any> {
    return (state, action) => {
        if (action.type === UserLogoutActions.logoutComplete.type) {
            return reducer(undefined, action);
        }
        return reducer(state, action);
    };
}