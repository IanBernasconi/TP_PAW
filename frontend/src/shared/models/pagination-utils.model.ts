import { URI } from "../types";


export enum linkTypes {
    FIRST = 'first',
    LAST = 'last',
    NEXT = 'next',
    PREVIOUS = 'prev'
}

export class Links {
    links: { [key: string]: string };

    constructor(links: { [key: string]: string }) {
        this.links = links;
    }

    getLink(type: linkTypes): URI | undefined {
        return this.links[type];
    }

    getFirstLink(): URI | undefined {
        return this.getLink(linkTypes.FIRST);
    }

    setFirstLink(uri: URI) {
        this.links[linkTypes.FIRST] = uri;
    }

    getLastLink(): URI | undefined {
        return this.getLink(linkTypes.LAST);
    }

    setLastLink(uri: URI) {
        this.links[linkTypes.LAST] = uri;
    }

    getNextLink(): URI | undefined {
        return this.getLink(linkTypes.NEXT);
    }

    getPreviousLink(): URI | undefined {
        return this.getLink(linkTypes.PREVIOUS);
    }

    getCurrentPage(): number {
        return this.getNextLink() ? parseInt(this.getNextLink()!.split('page=')[1]) - 1 : this.getPagesQuantity();
    }

    getPagesQuantity(): number {
        return this.getLastLink() ? parseInt(this.getLastLink()!.split('page=')[1]) : 1;
    }

    isLastPage(): boolean {
        return this.getNextLink() === undefined;
    }
}

export function parseLinks(linksHeader: string): { [key: string]: string } {
    const links: { [key: string]: string } = {};

    if (linksHeader) {
        const linkEntries = linksHeader.split(', ');

        linkEntries.forEach(entry => {
            const [link, rel] = entry.split('; ');

            const url = link.slice(1, -1); // Remove angle brackets around the URL
            const key = rel.split('=')[1].slice(1, -1); // Remove quotes around the rel value

            links[key] = url;
        });
    }
    return links;
}
