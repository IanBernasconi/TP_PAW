import { query } from "@angular/animations";
import { Directive } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";


@Directive()
export class PaginatedBaseComponent {

    constructor(protected route: ActivatedRoute, protected router: Router) { }

    protected persistPageToURL(page: number) {
        let queryParams: { page: number | null } = { page: page };
        if (page <= 0) {
            queryParams = { page: null };
        }
        this.router.navigate([], {
            queryParamsHandling: 'merge',
            queryParams: queryParams,
            replaceUrl: true
        });
    }

    protected getPageFromURL(): number {
        let initialPage = this.route.snapshot.queryParams['page'] ?? 0;
        if (initialPage < 0) {
            initialPage = 0;
        }
        return initialPage;
    }

}